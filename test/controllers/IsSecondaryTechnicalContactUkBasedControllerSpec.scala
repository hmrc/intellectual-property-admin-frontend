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
import forms.IsSecondaryTechnicalContactUkBasedFormProvider
import models.{AfaId, NormalMode, TechnicalContact, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{IsSecondaryTechnicalContactUkBasedPage, WhoIsSecondaryTechnicalContactPage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IsSecondaryTechnicalContactUkBasedView

import scala.concurrent.Future

class IsSecondaryTechnicalContactUkBasedControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val SecondaryTechnicalContact: TechnicalContact = TechnicalContact("companyName", "name", "telephone", "email")

  val formProvider        = new IsSecondaryTechnicalContactUkBasedFormProvider()
  val form: Form[Boolean] = formProvider(SecondaryTechnicalContact.contactName)

  private val baseUserAnswers =
    UserAnswers(afaId).set(WhoIsSecondaryTechnicalContactPage, SecondaryTechnicalContact).success.value

  lazy private val isSecondaryTechnicalContactUkBasedRoute =
    routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(NormalMode, afaId).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, isSecondaryTechnicalContactUkBasedRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, isSecondaryTechnicalContactUkBasedRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "IsSecondaryTechnicalContactUkBased Controller" must {

    "return OK and the correct view for a GET" in {

      val application: Application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result: Future[Result] = route(application, getRequest()).value

      val view = application.injector.instanceOf[IsSecondaryTechnicalContactUkBasedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, SecondaryTechnicalContact.contactName, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers: UserAnswers = baseUserAnswers.set(IsSecondaryTechnicalContactUkBasedPage, true).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view: IsSecondaryTechnicalContactUkBasedView =
        application.injector.instanceOf[IsSecondaryTechnicalContactUkBasedView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, SecondaryTechnicalContact.contactName, afaId)(getRequest(), messages).toString

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
        FakeRequest(POST, isSecondaryTechnicalContactUkBasedRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm: Form[Boolean] = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IsSecondaryTechnicalContactUkBasedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, SecondaryTechnicalContact.contactName, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None).build()

      val result: Future[Result] = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a GET if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if Rights Holder Contact Name has not been answered" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result: Future[Result] = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

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
