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
import forms.SecondaryTechnicalContactInternationalAddressFormProvider
import models.{AfaId, InternationalAddress, NormalMode, TechnicalContact, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{SecondaryTechnicalContactInternationalAddressPage, WhoIsSecondaryTechnicalContactPage}
import play.api.Application
import play.api.data.Form
import play.api.i18n.{Lang, Messages}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.SecondaryTechnicalContactInternationalAddressView

import java.util.Locale
import scala.concurrent.Future

class SecondaryTechnicalContactInternationalAddressControllerSpec
    extends SpecBase
    with MockitoSugar
    with LockAfaChecks {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val secondaryTechnicalContact: TechnicalContact = TechnicalContact("companyName", "name", "telephone", "email")

  val formProvider                             = new SecondaryTechnicalContactInternationalAddressFormProvider()
  private def form: Form[InternationalAddress] = formProvider(stubMessages)

  lazy val applicantSecondaryTechnicalContactInternationalAddressRoute: String =
    routes.SecondaryTechnicalContactInternationalAddressController.onPageLoad(NormalMode, afaId).url

  private val baseUserAnswers =
    UserAnswers(afaId).set(WhoIsSecondaryTechnicalContactPage, secondaryTechnicalContact).success.value

  val validAnswer: InternationalAddress = InternationalAddress("line 1", None, "town", "country", None)

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, applicantSecondaryTechnicalContactInternationalAddressRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, applicantSecondaryTechnicalContactInternationalAddressRoute)
      .withFormUrlEncodedBody(("line1", "line 1"), ("town", "town"), ("country", "country"))

  "SecondaryTechnicalContactInternationalAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application: Application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val view: SecondaryTechnicalContactInternationalAddressView =
        application.injector.instanceOf[SecondaryTechnicalContactInternationalAddressView]

      val result: Future[Result] = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, secondaryTechnicalContact.contactName, afaId)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers: UserAnswers =
        baseUserAnswers.set(SecondaryTechnicalContactInternationalAddressPage, validAnswer).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view: SecondaryTechnicalContactInternationalAddressView =
        application.injector.instanceOf[SecondaryTechnicalContactInternationalAddressView]

      val result: Future[Result] = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, secondaryTechnicalContact.contactName, afaId)(
          getRequest,
          messages
        ).toString

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

      val result: Future[Result] = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, applicantSecondaryTechnicalContactInternationalAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm: Form[InternationalAddress] = form.bind(Map("value" -> "invalid value"))

      val view: SecondaryTechnicalContactInternationalAddressView =
        application.injector.instanceOf[SecondaryTechnicalContactInternationalAddressView]

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, secondaryTechnicalContact.contactName, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a GET if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, postRequest).value

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
