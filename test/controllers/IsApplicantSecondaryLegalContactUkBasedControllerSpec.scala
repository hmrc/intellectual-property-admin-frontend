/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.IsApplicantSecondaryLegalContactUkBasedFormProvider
import models.{AfaId, NormalMode, UserAnswers, WhoIsSecondaryLegalContact}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{IsApplicantSecondaryLegalContactUkBasedPage, WhoIsSecondaryLegalContactPage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IsApplicantSecondaryLegalContactUkBasedView

import scala.concurrent.Future

class IsApplicantSecondaryLegalContactUkBasedControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val secondaryLegalContact: WhoIsSecondaryLegalContact = WhoIsSecondaryLegalContact("name", "companyName", "telephone", "email")

  val formProvider = new IsApplicantSecondaryLegalContactUkBasedFormProvider()
  val form: Form[Boolean] = formProvider(secondaryLegalContact.contactName)

  private val baseUserAnswers = UserAnswers(afaId).set(WhoIsSecondaryLegalContactPage, secondaryLegalContact).success.value

  lazy private val isApplicantSecondaryLegalContactUkBasedRoute =
    routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(NormalMode, afaId).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, isApplicantSecondaryLegalContactUkBasedRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, isApplicantSecondaryLegalContactUkBasedRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "IsApplicantSecondaryLegalContactUkBased Controller" must {

    "return OK and the correct view for a GET" in {

      val application: Application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result: Future[Result] = route(application, getRequest()).value

      val view = application.injector.instanceOf[IsApplicantSecondaryLegalContactUkBasedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, secondaryLegalContact.contactName, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers: UserAnswers = baseUserAnswers.set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view: IsApplicantSecondaryLegalContactUkBasedView = application.injector.instanceOf[IsApplicantSecondaryLegalContactUkBasedView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, secondaryLegalContact.contactName, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val result: Future[Result] = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, isApplicantSecondaryLegalContactUkBasedRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm: Form[Boolean] = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IsApplicantSecondaryLegalContactUkBasedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, secondaryLegalContact.contactName, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a GET if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, postRequest()).value

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
