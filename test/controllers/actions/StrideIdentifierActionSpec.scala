/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import base.SpecBase
import com.google.inject.Inject
import controllers.routes
import play.api.Configuration
import play.api.mvc.{BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad() = authAction(_ => Results.Ok)
  }

  "Stride Identifier Action" when {

    "the user hasn't logged in" must {

      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        val config = application.injector.instanceOf[Configuration]

        val loginUrl = config.get[String]("login.url")

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new StrideIdentifierAction(
          new FakeFailingAuthConnector(new MissingBearerToken),
          config,
          bodyParsers,
          frontendAppConfig
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(loginUrl)
      }
    }

    "the user's session has expired" must {

      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        val config = application.injector.instanceOf[Configuration]

        val loginUrl = config.get[String]("login.url")

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new StrideIdentifierAction(
          new FakeFailingAuthConnector(new BearerTokenExpired),
          config,
          bodyParsers,
          frontendAppConfig
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val config = application.injector.instanceOf[Configuration]

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new StrideIdentifierAction(
          new FakeFailingAuthConnector(new InsufficientEnrolments),
          config,
          bodyParsers,
          frontendAppConfig
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user used an unaccepted auth provider" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val config = application.injector.instanceOf[Configuration]

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new StrideIdentifierAction(
          new FakeFailingAuthConnector(new UnsupportedAuthProvider),
          config,
          bodyParsers,
          frontendAppConfig
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[A] =
    Future.failed(exceptionToReturn)
}
