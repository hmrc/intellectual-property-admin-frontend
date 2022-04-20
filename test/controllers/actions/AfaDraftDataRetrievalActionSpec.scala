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

import generators.ModelGenerators
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{AfaId, UserAnswers}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService

import scala.concurrent.Future

class AfaDraftDataRetrievalActionSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with ScalaFutures
  with MockitoSugar with ModelGenerators with OptionValues {

  val mockAfaService: AfaService = mock[AfaService]

  val userAnswersId: AfaId = arbitrary[AfaId].sample.value

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[AfaService].toInstance(mockAfaService)
      ).build()
  }

  def harness(afaId: AfaId, f: OptionalDataRequest[AnyContent] => Unit): Unit = {

    lazy val actionProvider = app.injector.instanceOf[AfaDraftDataRetrievalActionProviderImpl]

    actionProvider(afaId).invokeBlock(IdentifierRequest(FakeRequest(GET, "/"), "", ""), {
      request: OptionalDataRequest[AnyContent] =>
        f(request)
        Future.successful(Results.Ok)
    }).futureValue
  }

  "an afa data retrieval action" - {

    "must return an OptionalDataRequest with an empty userAnswers" - {

      "when there are no existing answers for this AFA ID" in {

        when(mockAfaService.getDraft(any())(any())) thenReturn Future.successful(None)

        harness(userAnswersId, {
          request =>

            request.userAnswers mustNot be (defined)
        })
      }
    }

    "must return OptionalDataRequest with some define userAnswers" - {

      "when there are existing answers for this AFA ID" in {

        when(mockAfaService.getDraft(any())(any())) thenReturn Future.successful(Some(UserAnswers(userAnswersId)))

        harness(userAnswersId, {
          request =>

            request.userAnswers mustBe defined
        })
      }
    }
  }
}
