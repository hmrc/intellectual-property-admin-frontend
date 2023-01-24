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

import constants.ContactsConstants._
import base.SpecBase
import forms.SelectTechnicalContactFormProvider
import models.{AfaId, CompanyApplying, ContactOptions, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.{CompanyApplyingPage, SelectTechnicalContactPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AfaService, ContactsService}
import views.html.SelectTechnicalContactView

import scala.concurrent.Future

class SelectTechnicalContactControllerSpec
    extends SpecBase
    with MockitoSugar
    with LockAfaChecks
    with BeforeAndAfterEach {

  def onwardRoute: Call                  = Call("GET", "/foo")
  private val contactsService            = mock[ContactsService]
  private def form: Form[ContactOptions] = formProvider()

  val afaId: AfaId             = userAnswersId
  val formProvider             = new SelectTechnicalContactFormProvider()
  val invalidValue             = "invalid value"
  val userAnswers: UserAnswers = emptyUserAnswers
    .set(CompanyApplyingPage, CompanyApplying(companyApplyingName, None))
    .success
    .value

  lazy val selectTechnicalContactRoute: String =
    routes.SelectTechnicalContactController.onPageLoad(NormalMode, afaId).url

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, selectTechnicalContactRoute)

  def postRequest(contactOption: ContactOptions): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, selectTechnicalContactRoute)
      .withFormUrlEncodedBody(("value", contactOption.toString))

  def postRequestRandomValue: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, selectTechnicalContactRoute)
      .withFormUrlEncodedBody(("value", "invalidValue"))

  override def beforeEach(): Unit =
    Mockito.reset(contactsService)

  "SelectTechnicalContactController" must {
    "return OK and the correct view for a GET" in {

      when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[SelectTechnicalContactView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId, companyApplyingName, Seq.empty)(getRequest, messages).toString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(CompanyApplyingPage, CompanyApplying(companyApplyingName, None))
        .success
        .value
        .set(SelectTechnicalContactPage, ContactOptions.RepresentativeContact)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[SelectTechnicalContactView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(ContactOptions.RepresentativeContact), NormalMode, afaId, companyApplyingName, Seq.empty)(
          getRequest,
          messages
        ).toString

      application.stop()
    }

    "redirect to the next page when a valid radio button was selected" must {
      "redirect the next page when isContactBasedInUk is true and address exists" in {

        val mockAfaService = mock[AfaService]
        when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

        when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
        when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
        when(contactsService.getContactIsUKBased(any())(any())).thenReturn(Some(true))
        when(contactsService.getContactUKAddress(any())(any())).thenReturn(Some(ukAddress))
        when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(None)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[AfaService].toInstance(mockAfaService),
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[ContactsService].toInstance(contactsService)
            )
            .build()

        val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        application.stop()
      }

      "redirect to the next page when isContactBasedInUk is false and international address exists" in {

        val mockAfaService = mock[AfaService]
        when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

        when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
        when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
        when(contactsService.getContactIsUKBased(any())(any())).thenReturn(Some(false))
        when(contactsService.getContactUKAddress(any())(any())).thenReturn(None)
        when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(Some(internationalAddress))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[AfaService].toInstance(mockAfaService),
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[ContactsService].toInstance(contactsService)
            )
            .build()

        val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        application.stop()
      }

      "redirect to the next page when someoneElse radio button was selected" in {

        val mockAfaService = mock[AfaService]
        when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

        when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
        when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
        when(contactsService.getContactIsUKBased(any())(any())).thenReturn(None)
        when(contactsService.getContactUKAddress(any())(any())).thenReturn(None)
        when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(None)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[AfaService].toInstance(mockAfaService),
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[ContactsService].toInstance(contactsService)
            )
            .build()

        val result = route(application, postRequest(ContactOptions.SomeoneElse)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        application.stop()
      }
    }

    "go to session expired when invalid contact data" must {
      "redirect to session expired controller when there is None for isContactBasedInUk" in {

        when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
        when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
        when(contactsService.getContactIsUKBased(any())(any())).thenReturn(None)
        when(contactsService.getContactUKAddress(any())(any())).thenReturn(Some(ukAddress))
        when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(None)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[ContactsService].toInstance(contactsService)
            )
            .build()

        val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
        application.stop()
      }
      "redirect to session expired controller when there is true for contactUKAddress and uk address" +
        "is none" in {

          when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
          when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
          when(contactsService.getContactIsUKBased(any())(any())).thenReturn(Some(true))
          when(contactsService.getContactUKAddress(any())(any())).thenReturn(None)
          when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(None)

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
                bind[ContactsService].toInstance(contactsService)
              )
              .build()

          val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
          application.stop()
        }
      "redirect to session expired controller when there is false for contactUKAddress and internation address" +
        "is none" in {

          when(contactsService.contactsToRadioOptions(any())(any())).thenReturn(Seq.empty)
          when(contactsService.getContact(any())(any())).thenReturn(technicalContactDetails)
          when(contactsService.getContactIsUKBased(any())(any())).thenReturn(Some(false))
          when(contactsService.getContactUKAddress(any())(any())).thenReturn(None)
          when(contactsService.getContactInternationalAddress(any())(any())).thenReturn(None)

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
                bind[ContactsService].toInstance(contactsService)
              )
              .build()

          val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
          application.stop()
        }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(Some(userAnswers)).build()

      val request =
        FakeRequest(POST, selectTechnicalContactRoute)
          .withFormUrlEncodedBody(("value", invalidValue))

      val boundForm = form.bind(Map("value" -> invalidValue))

      val view = application.injector.instanceOf[SelectTechnicalContactView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId, companyApplyingName, Seq.empty)(fakeRequest, messages).toString

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

      val result = route(application, postRequest(ContactOptions.RepresentativeContact)).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      redirectIfLocked(afaId, () => getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, () => postRequestRandomValue)
    }
  }
}
