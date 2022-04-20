/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.UnlockAfaFormProvider
import models.{AfaId, CompanyApplying, Lock, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.CompanyApplyingPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LockService
import views.html.UnlockAfaView

import scala.concurrent.Future

class UnlockAfaControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new UnlockAfaFormProvider()
  val form: Form[Boolean] = formProvider()

  private lazy val unlockAfaRoute = routes.UnlockAfaController.onPageLoad(afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, unlockAfaRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, unlockAfaRoute)
      .withFormUrlEncodedBody(("value", "true"))

  val existingLock: Lock = Lock(afaId, "id", "name")

  val companyApplying: Option[String] = Some("AN")

  private val baseAnswers = UserAnswers(afaId).set(CompanyApplyingPage, CompanyApplying("Applicant Name", Some("AN"))).success.value

  "UnlockAfa Controller" must {

    "return OK and the correct view for a GET" in {

      val mockLockService = mock[LockService]

      when(mockLockService.getExistingLock(any())(any())) thenReturn Future.successful(Some(existingLock))

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[UnlockAfaView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, afaId, existingLock.name, companyApplying)(getRequest(), messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when the Intellectual Property Owner question has not been answered" in {

      val mockLockService = mock[LockService]

      when(mockLockService.getExistingLock(any())(any())) thenReturn Future.successful(Some(existingLock))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[UnlockAfaView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, afaId, existingLock.name, None)(getRequest(), messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when there is no draft data" in {

      val mockLockService = mock[LockService]

      when(mockLockService.getExistingLock(any())(any())) thenReturn Future.successful(Some(existingLock))

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[UnlockAfaView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, afaId, existingLock.name, None)(getRequest(), messages).toString

      application.stop()
    }

    "throw an exception for a GET if the Afa is not locked" in {

      val mockLockService = mock[LockService]

      when(mockLockService.getExistingLock(any())(any())) thenReturn Future.successful(None)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val result = route(application, getRequest()).value

      whenReady(result.failed) {
        _ mustBe an[Exception]
      }
    }

    "redirect to the next page when valid data is submitted" in {

      val mockLockService = mock[LockService]

      when(mockLockService.replaceLock(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[LockService].toInstance(mockLockService)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val mockLockService = mock[LockService]

      when(mockLockService.getExistingLock(any())(any())) thenReturn Future.successful(Some(existingLock))

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val request =
        FakeRequest(POST, unlockAfaRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[UnlockAfaView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, afaId, existingLock.name, companyApplying)(fakeRequest, messages).toString

      application.stop()
    }

    "replace the lock with a new one if the user answers Yes" in {

      val mockLockService = mock[LockService]

      when(mockLockService.replaceLock(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      route(application, postRequest()).value.futureValue

      verify(mockLockService, times(1)).replaceLock(any())(any())
    }

    "not replace the lock if the user answers No" in {

      val mockLockService = mock[LockService]

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[LockService].toInstance(mockLockService))
          .build()

      val request =
        FakeRequest(POST, unlockAfaRoute)
          .withFormUrlEncodedBody(("value", "false"))

      route(application, request)

      verify(mockLockService, times(0)).replaceLock(any())(any())
    }
  }
}
