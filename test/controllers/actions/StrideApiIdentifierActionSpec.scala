/*
 * Copyright 2022 HM Revenue & Customs
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
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StrideApiIdentifierActionSpec extends SpecBase with Matchers with GuiceOneAppPerSuite with Injecting with MockitoSugar {

  class Harness(authAction: ApiIdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  "Stride API Identifier Action" when {

    "the user is authorised and has required retrievals" must {

      "return OK" in {

        val authConnector = mock[AuthConnector]

        val credentials = Credentials("id", "type")
        val name = Name(Some("name"), None)
        val retrieval = new ~(Some(credentials), Some(name))

        when(authConnector.authorise[Option[Credentials] ~ Option[Name]](any(), any())(any(), any()))
          .thenReturn(Future.successful(retrieval))

        val config = inject[Configuration]
        val bodyParsers = inject[BodyParsers.Default]

        val authAction = new StrideApiIdentifierAction(authConnector, config, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe OK
      }
    }

    "when the user is authorised but does not have the required retrievals" must {

      "return Unauthorised" in {

        val authConnector = mock[AuthConnector]

        val credentials = Credentials("id", "type")
        val name = Name(None, None)
        val retrieval = new ~(Some(credentials), Some(name))

        when(authConnector.authorise[Option[Credentials] ~ Option[Name]](any(), any())(any(), any()))
          .thenReturn(Future.successful(retrieval))

        val config = inject[Configuration]
        val bodyParsers = inject[BodyParsers.Default]

        val authAction = new StrideApiIdentifierAction(authConnector, config, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe UNAUTHORIZED
      }
    }

    "the user hasn't logged in" must {

      "return Unauthorised" in {

        val config = inject[Configuration]
        val bodyParsers = inject[BodyParsers.Default]

        val authAction = new StrideApiIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), config, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe UNAUTHORIZED
      }
    }

    "the user's session has expired" must {

      "return Unauthorised" in {

        val config = inject[Configuration]
        val bodyParsers = inject[BodyParsers.Default]

        val authAction = new StrideApiIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), config, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe UNAUTHORIZED
      }
    }

    "when the user used an unaccepted auth provider" must {

      "return Unauthorised" in {

        val config = inject[Configuration]
        val bodyParsers = inject[BodyParsers.Default]

        val authAction = new StrideApiIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), config, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe UNAUTHORIZED
      }
    }
  }
}
