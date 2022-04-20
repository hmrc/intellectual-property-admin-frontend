/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.IpRightsSupplementaryProtectionCertificateTypeFormProvider
import models.{AfaId, IpRightsSupplementaryProtectionCertificateType, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.IpRightsSupplementaryProtectionCertificateTypePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IpRightsSupplementaryProtectionCertificateTypeView

import scala.concurrent.Future

class IpRightsSupplementaryProtectionCertificateTypeControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val index = 0

  lazy val ipRightsSupplementaryProtectionCertificateTypeRoute: String =
    routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(NormalMode, index, afaId).url

  val formProvider = new IpRightsSupplementaryProtectionCertificateTypeFormProvider()
  private def form = formProvider()

  override val emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ipRightsSupplementaryProtectionCertificateTypeRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, ipRightsSupplementaryProtectionCertificateTypeRoute)
      .withFormUrlEncodedBody(("value", IpRightsSupplementaryProtectionCertificateType.values.head.toString))

  "IpRightsSupplementaryProtectionCertificateType Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[IpRightsSupplementaryProtectionCertificateTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId)
          .set(IpRightsSupplementaryProtectionCertificateTypePage(index), IpRightsSupplementaryProtectionCertificateType.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[IpRightsSupplementaryProtectionCertificateTypeView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(IpRightsSupplementaryProtectionCertificateType.values.head), NormalMode, index, afaId)(getRequest, messages).toString

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
        FakeRequest(POST, ipRightsSupplementaryProtectionCertificateTypeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[IpRightsSupplementaryProtectionCertificateTypeView]

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

      redirectIfLocked(afaId, getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, postRequest)
    }
  }
}
