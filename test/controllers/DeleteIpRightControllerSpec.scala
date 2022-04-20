/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.DeleteIpRightFormProvider
import models.{AfaId, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalacheck.Arbitrary.arbitrary
import pages.DeleteIpRightPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.DeleteIpRightView

import scala.concurrent.Future

class DeleteIpRightControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks with IprIndexValidation {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new DeleteIpRightFormProvider()

  private def form = formProvider()

  lazy val deleteIpRightRoute: String = routes.DeleteIpRightController.onPageLoad(NormalMode, afaId, 0).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, deleteIpRightRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, deleteIpRightRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "DeleteIpRight Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[DeleteIpRightView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId, 0)(getRequest(), messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, deleteIpRightRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeleteIpRightView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId, 0)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.DeleteIpRightController.onPageLoad(NormalMode, afaId, index).url

        FakeRequest(GET, route)
      }

      validateOnIprIndex(
        arbitrary[Boolean],
        DeleteIpRightPage.apply,
        getForIndex
      )
    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.DeleteIpRightController.onPageLoad(NormalMode, afaId, index).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "true"))
      }

      validateOnIprIndex(
        arbitrary[Boolean],
        DeleteIpRightPage.apply,
        postForIndex
      )
    }

    "for a GET" must {

      redirectIfLocked(afaId, getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, postRequest)
    }
  }
}
