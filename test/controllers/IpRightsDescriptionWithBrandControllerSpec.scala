/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.IpRightsDescriptionWithBrandFormProvider
import models.{AfaId, IpRightsDescriptionWithBrand, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import pages.IpRightsDescriptionWithBrandPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IpRightsDescriptionWithBrandView

import scala.concurrent.Future

class IpRightsDescriptionWithBrandControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks with IprIndexValidation {

  def onwardRoute: Call = Call("GET", "/foo")

  val index = 0

  val afaId: AfaId = userAnswersId

  val formProvider = new IpRightsDescriptionWithBrandFormProvider()
  private def form = formProvider()

  lazy val ipRightsDescriptionWithBrandRoute: String = routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, index, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  val validAnswer: IpRightsDescriptionWithBrand = IpRightsDescriptionWithBrand("brand", "value 2")

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ipRightsDescriptionWithBrandRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, ipRightsDescriptionWithBrandRoute)
      .withFormUrlEncodedBody(("brand", "value 1"), ("value", "value 2"))

  "IpRightsDescriptionWithBrand Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[IpRightsDescriptionWithBrandView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(IpRightsDescriptionWithBrandPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[IpRightsDescriptionWithBrandView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, index, afaId)(getRequest, messages).toString

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
        FakeRequest(POST, ipRightsDescriptionWithBrandRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[IpRightsDescriptionWithBrandView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, afaId)(fakeRequest, messages).toString

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
        val route = routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, index, afaId).url

        FakeRequest(GET, route)
      }

      validateOnIprIndex(
        arbitrary[IpRightsDescriptionWithBrand],
        IpRightsDescriptionWithBrandPage.apply,
        getForIndex
      )
    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, index, afaId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("brand", "value 1"), ("description", "value 2"))
      }

      validateOnIprIndex(
        arbitrary[IpRightsDescriptionWithBrand],
        IpRightsDescriptionWithBrandPage.apply,
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
