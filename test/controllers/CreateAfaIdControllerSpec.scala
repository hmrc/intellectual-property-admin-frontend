/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import connectors.AfaConnector
import generators.ModelGenerators
import models.AfaId
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CreateAfaIdControllerSpec extends SpecBase with ScalaCheckPropertyChecks with MockitoSugar with TryValues with ModelGenerators {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "CreateAfaId Controller" must {

    "redirect to the next page and create an AFA ID for a GET" in {
      forAll(arbitrary[AfaId]) {
        afaId =>

          val afaConnector = mock[AfaConnector]

          when(afaConnector.getNextAfaId()(any(), any())) thenReturn Future.successful(afaId)

          val application = {

            import play.api.inject._

            applicationBuilder(userAnswers = None)
              .overrides(
                bind[AfaConnector].toInstance(afaConnector)
              ).build()
          }

          val request = FakeRequest(GET, controllers.routes.CreateAfaIdController.onPageLoad().url)

          val result = route(application, request).value

          val navigator = application.injector.instanceOf[Navigator]

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual navigator.firstPage(afaId).url

          application.stop()
      }
    }
  }
}
