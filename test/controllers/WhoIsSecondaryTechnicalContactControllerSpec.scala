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
import forms.WhoIsSecondaryTechnicalContactFormProvider
import models.{AfaId, CompanyApplying, NormalMode, TechnicalContact, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{CompanyApplyingPage, WhoIsSecondaryTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{Lang, Messages}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.WhoIsSecondaryTechnicalContactView

import java.util.Locale
import scala.concurrent.Future

class WhoIsSecondaryTechnicalContactControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val afaId: AfaId = userAnswersId

  val formProvider = new WhoIsSecondaryTechnicalContactFormProvider()

  private def form: Form[TechnicalContact] = formProvider(stubMessages)

  lazy val whoIsSecondaryTechnicalContactRoute: String     =
    routes.WhoIsSecondaryTechnicalContactController.onPageLoad(NormalMode, afaId).url
  lazy val whoIsSecondaryTechnicalContactRoutePost: String =
    routes.WhoIsSecondaryTechnicalContactController.onSubmit(NormalMode, afaId).url

  val validAnswer: TechnicalContact = TechnicalContact("companyName", "name", "123123123", "email@example.com")

  override val emptyUserAnswers: UserAnswers =
    UserAnswers(afaId).set(CompanyApplyingPage, CompanyApplying(validAnswer.contactName, None)).success.value

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, whoIsSecondaryTechnicalContactRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, whoIsSecondaryTechnicalContactRoutePost)
      .withFormUrlEncodedBody(
        ("companyName", "company Name"),
        ("name", "contact Name"),
        ("telephone", "contact Telephone"),
        ("email", "email@example.com")
      )

  "WhoIsSecondaryTechnicalContact Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[WhoIsSecondaryTechnicalContactView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId, validAnswer.contactName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(WhoIsSecondaryTechnicalContactPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[WhoIsSecondaryTechnicalContactView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, afaId, validAnswer.contactName)(getRequest, messages).toString

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

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whoIsSecondaryTechnicalContactRoutePost)
          .withFormUrlEncodedBody(
            ("companyName", "company name"),
            ("name", ""),
            ("telephone", "contact Telephone"),
            ("email", "contact email")
          )

      val boundForm = form.bind(
        Map(
          "companyName" -> "company name",
          "name"        -> "",
          "telephone"   -> "contact Telephone",
          "email"       -> "contact email"
        )
      )

      val view = application.injector.instanceOf[WhoIsSecondaryTechnicalContactView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId, validAnswer.contactName)(fakeRequest, messages).toString

      application.stop()
    }

    "return a Bad Request and error when all data is entered except the optional company name name as it's controller enforced" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whoIsSecondaryTechnicalContactRoutePost)
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
