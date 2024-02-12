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
import forms.IsRepresentativeContactLegalContactFormProvider
import models.{AfaId, NormalMode, RepresentativeDetails, UkAddress, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{IsRepresentativeContactLegalContactPage, IsRepresentativeContactUkBasedPage, RepresentativeContactUkAddressPage, RepresentativeDetailsPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IsRepresentativeContactLegalContactView

import scala.concurrent.Future

class IsRepresentativeContactLegalContactControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new IsRepresentativeContactLegalContactFormProvider()

  private def form: Form[Boolean] = formProvider()

  lazy val isRepresentativeContactLegalContactRoute: String =
    routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, afaId).url

  val representativeContactName                    = "representative contact name"
  val representativeContact: RepresentativeDetails =
    RepresentativeDetails(representativeContactName, "companyName", "telephone", "email", Some("role"))
  val ukAddress: UkAddress                         = UkAddress("Grange Central", None, "Telford", None, "TF34ER")

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  val userAnswers: UserAnswers = UserAnswers(afaId)
    .set(RepresentativeDetailsPage, representativeContact)
    .success
    .value

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, isRepresentativeContactLegalContactRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, isRepresentativeContactLegalContactRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "IsRepresentativeContactLegalContact Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[IsRepresentativeContactLegalContactView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId, representativeContactName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersWithAnswer = userAnswers
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeDetailsPage, representativeContact)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, ukAddress)
        .success
        .value
        .set(IsRepresentativeContactLegalContactPage, true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswersWithAnswer)).build()

      val view = application.injector.instanceOf[IsRepresentativeContactLegalContactView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, afaId, representativeContactName)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val userAnswersWithoutAnswer = userAnswers
        .set(RepresentativeDetailsPage, representativeContact)
        .success
        .value
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, ukAddress)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithoutAnswer))
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

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, isRepresentativeContactLegalContactRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IsRepresentativeContactLegalContactView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId, representativeContactName)(fakeRequest, messages).toString

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

    "for a GET" must {

      redirectIfLocked(afaId, () => getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, () => postRequest)
    }
  }
}
