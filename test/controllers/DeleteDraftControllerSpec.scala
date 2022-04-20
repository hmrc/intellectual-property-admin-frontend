/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.DeleteDraftFormProvider
import models.{AfaId, CompanyApplying, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.CompanyApplyingPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AfaService, LockService}
import views.html.DeleteDraftView

import scala.concurrent.Future

class DeleteDraftControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks with ScalaFutures {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new DeleteDraftFormProvider()
  private def form: Form[Boolean] = formProvider()

  lazy val deleteDraftRoute: String = routes.DeleteDraftController.onPageLoad(afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, deleteDraftRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, deleteDraftRoute)
      .withFormUrlEncodedBody(("value", "true"))

  val companyApplying = "AN"

  private val baseAnswers = UserAnswers(afaId).set(CompanyApplyingPage, CompanyApplying("Applicant Name", Some("AN"))).success.value

  "DeleteDraft Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[DeleteDraftView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, afaId, companyApplying)(getRequest(), messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when company applying has not been answered yet" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[DeleteDraftView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, afaId, "Unknown applicant (or company) name")(getRequest(), messages).toString

      application.stop()
    }

    "delete the draft and lock, and redirect to the next page when the user answers Yes" in {

      val mockAfaService = mock[AfaService]
      val mockLockService = mock[LockService]

      when(mockAfaService.removeDraft(any())(any())) thenReturn Future.successful(Some(baseAnswers))
      when(mockLockService.removeLock(any())(any())) thenReturn Future.successful(())

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService),
            bind[LockService].toInstance(mockLockService)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      result.futureValue

      verify(mockAfaService, times(1)).removeDraft(eqTo(afaId))(any())
      verify(mockLockService, times(1)).removeLock(eqTo(afaId))(any())

      application.stop()
    }

    "not delete the draft or lock when the user answers No" in {

      val mockAfaService = mock[AfaService]
      val mockLockService = mock[LockService]

      when(mockAfaService.removeDraft(any())(any())) thenReturn Future.successful(Some(baseAnswers))
      when(mockLockService.removeLock(any())(any())) thenReturn Future.successful(())

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService),
            bind[LockService].toInstance(mockLockService)
          )
          .build()

      val request = FakeRequest(POST, deleteDraftRoute)
        .withFormUrlEncodedBody(("value", "false"))

      route(application, request).value.futureValue

      verify(mockAfaService, times(0)).removeDraft(eqTo(afaId))(any())
      verify(mockLockService, times(0)).removeLock(eqTo(afaId))(any())

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, deleteDraftRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeleteDraftView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, afaId, companyApplying)(fakeRequest, messages).toString

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
