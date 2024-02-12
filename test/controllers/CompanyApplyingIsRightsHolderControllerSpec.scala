/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import base.SpecBase
import forms.CompanyApplyingIsRightsHolderFormProvider
import models.{AfaId, CompanyApplying, CompanyApplyingIsRightsHolder, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{CompanyApplyingIsRightsHolderPage, CompanyApplyingPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.CompanyApplyingIsRightsHolderView

import scala.concurrent.Future
import org.scalacheck.Arbitrary.arbitrary
import services.AfaService

class CompanyApplyingIsRightsHolderControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  lazy val companyApplyingIsRightsHolderRoute: String =
    routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, afaId).url

  val companyName: String = arbitrary[String].sample.value

  val formProvider = new CompanyApplyingIsRightsHolderFormProvider()
  private def form = formProvider()

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, companyApplyingIsRightsHolderRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, companyApplyingIsRightsHolderRoute)
      .withFormUrlEncodedBody(("value", CompanyApplyingIsRightsHolder.values.head.toString))

  "CompanyApplyingIsRightsHolder Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(CompanyApplyingPage, CompanyApplying(companyName, None)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[CompanyApplyingIsRightsHolderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId, companyName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(CompanyApplyingPage, CompanyApplying(companyName, None))
        .success
        .value
        .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.values.head)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[CompanyApplyingIsRightsHolderView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(CompanyApplyingIsRightsHolder.values.head), NormalMode, afaId, companyName)(
          getRequest,
          messages
        ).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = UserAnswers(userAnswersId)
        .set(CompanyApplyingPage, CompanyApplying(companyName, None))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(CompanyApplyingPage, CompanyApplying(companyName, None)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, companyApplyingIsRightsHolderRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[CompanyApplyingIsRightsHolderView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId, companyName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to session expired when the company name has not been provided" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      redirectIfLocked(afaId, () => getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, () => postRequest)
    }
  }
}
