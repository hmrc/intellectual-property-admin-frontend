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
import forms.ApplicantLegalContactInternationalAddressFormProvider
import models.{AfaId, ApplicantLegalContact, InternationalAddress, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ApplicantLegalContactInternationalAddressPage, ApplicantLegalContactPage}
import play.api.data.Form
import play.api.i18n.{Lang, Messages}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.ApplicantLegalContactInternationalAddressView

import java.util.Locale
import scala.concurrent.Future

class ApplicantLegalContactInternationalAddressControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val afaId: AfaId = userAnswersId

  val applicantLegalContact: ApplicantLegalContact =
    ApplicantLegalContact("name", "companyName", "telephone", None, "email")

  val formProvider                             = new ApplicantLegalContactInternationalAddressFormProvider()
  private def form: Form[InternationalAddress] = formProvider(stubMessages)

  lazy val applicantLegalContactInternationalAddressRoute: String =
    routes.ApplicantLegalContactInternationalAddressController.onPageLoad(NormalMode, afaId).url

  private val baseUserAnswers = UserAnswers(afaId).set(ApplicantLegalContactPage, applicantLegalContact).success.value

  val validAnswer: InternationalAddress = InternationalAddress("line 1", None, "town", "country", None)

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, applicantLegalContactInternationalAddressRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, applicantLegalContactInternationalAddressRoute)
      .withFormUrlEncodedBody(("line1", "line 1"), ("town", "town"), ("country", "country"))

  "ApplicantLegalContactInternationalAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val view = application.injector.instanceOf[ApplicantLegalContactInternationalAddressView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, applicantLegalContact.name, afaId)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers.set(ApplicantLegalContactInternationalAddressPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[ApplicantLegalContactInternationalAddressView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, applicantLegalContact.name, afaId)(getRequest, messages).toString

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

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, applicantLegalContactInternationalAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ApplicantLegalContactInternationalAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, applicantLegalContact.name, afaId)(fakeRequest, messages).toString

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

    "redirect to Session Expired for a GET if Rights Holder Contact Name has not been answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if Rights Holder Contact Name has not been answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, postRequest).value

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
