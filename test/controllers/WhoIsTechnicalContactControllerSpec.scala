/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.WhoIsTechnicalContactFormProvider
import models.{AfaId, NormalMode, UserAnswers, TechnicalContact}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhoIsTechnicalContactPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.WhoIsTechnicalContactView

import scala.concurrent.Future

class WhoIsTechnicalContactControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new WhoIsTechnicalContactFormProvider()

  private def form: Form[TechnicalContact] = formProvider()

  lazy val whoIsTechnicalContactRoute: String = routes.WhoIsTechnicalContactController.onPageLoad(NormalMode, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  val validAnswer: TechnicalContact = TechnicalContact("company Name", "contact Name", "contact Telephone", "email@example.com")

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, whoIsTechnicalContactRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, whoIsTechnicalContactRoute)
      .withFormUrlEncodedBody(
        ("companyName", "company Name"),
        ("contactName", "contact Name"),
        ("contactTelephone", "contact Telephone"),
        ("contactEmail", "email@example.com")
      )

  "WhoIsTechnicalContact Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[WhoIsTechnicalContactView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(WhoIsTechnicalContactPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[WhoIsTechnicalContactView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, afaId)(getRequest(), messages).toString

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
        FakeRequest(POST, whoIsTechnicalContactRoute)
          .withFormUrlEncodedBody(
            ("companyName", "company name"),
            ("name", ""),
            ("telephone", "contact Telephone"),
            ("email", "contact Email")
          )

      val boundForm = form.bind(Map(
        "companyName" -> "company name",
        "name" -> "",
        "telephone" -> "contact Telephone",
        "email" -> "contact email"
      ))


      val view = application.injector.instanceOf[WhoIsTechnicalContactView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "return a Bad Request and error when all data is entered except the optional company name name as it's controller enforced" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whoIsTechnicalContactRoute)
          .withFormUrlEncodedBody(
            ("companyName", ""),
            ("name", "name"),
            ("telephone", "contact Telephone"),
            ("email", "contact Email")
          )


      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST


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


