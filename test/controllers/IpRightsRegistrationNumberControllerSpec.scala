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

import java.time.LocalDate
import base.SpecBase
import forms.IpRightsRegistrationNumberFormProvider
import models.{AfaId, IpRightsType, NiceClassId, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IpRightsRegistrationNumberView

import scala.concurrent.Future

class IpRightsRegistrationNumberControllerSpec
    extends SpecBase
    with IprIndexValidation
    with MockitoSugar
    with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val index = 0

  val ipRightsType: IpRightsType = IpRightsType.Copyright
  val ipRightsMessageName        = "registration"

  val formProvider = new IpRightsRegistrationNumberFormProvider()
  private def form = formProvider(ipRightsMessageName, Seq("firstRegistrationNumber"))

  lazy val ipRightsRegistrationNumberRoute: String =
    routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, index, afaId).url

  private val baseAnswers = UserAnswers(afaId).set(IpRightsTypePage(index), ipRightsType).success.value

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ipRightsRegistrationNumberRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, ipRightsRegistrationNumberRoute)
      .withFormUrlEncodedBody(("value", "answer"))

  "IpRightsRegistrationNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[IpRightsRegistrationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, afaId, ipRightsMessageName)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers.set(IpRightsRegistrationNumberPage(index), "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[IpRightsRegistrationNumberView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), NormalMode, index, afaId, ipRightsMessageName)(getRequest(), messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
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

    "submit a value if it already exists in the userAnswers in the same index" in {

      val userAnswers = baseAnswers.set(IpRightsRegistrationNumberPage(index), "1").success.value

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, ipRightsRegistrationNumberRoute)
          .withFormUrlEncodedBody(("value", "1"))

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, ipRightsRegistrationNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IpRightsRegistrationNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, afaId, ipRightsMessageName)(fakeRequest, messages).toString

      application.stop()
    }

    "not allow a duplicate registration numbers to be resubmitted, regardless of upper or lower case" in {

      val answers =
        emptyUserAnswers
          .set(IpRightsTypePage(0), IpRightsType.Trademark)
          .success
          .value
          .set(IpRightsRegistrationNumberPage(0), "firstRegNum")
          .success
          .value
          .set(IpRightsRegistrationEndPage(0), LocalDate.now())
          .success
          .value
          .set(IpRightsDescriptionPage(0), "desc")
          .success
          .value
          .set(IpRightsNiceClassPage(0, 0), NiceClassId.fromInt(12).value)
          .success
          .value
          .set(IpRightsTypePage(1), IpRightsType.Trademark)
          .success
          .value
          .set(IpRightsRegistrationNumberPage(1), "existingRegNum")
          .success
          .value
          .set(IpRightsRegistrationEndPage(1), LocalDate.now())
          .success
          .value
          .set(IpRightsDescriptionPage(1), "desc")
          .success
          .value
          .set(IpRightsNiceClassPage(1, 0), NiceClassId.fromInt(12).value)
          .success
          .value

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, ipRightsRegistrationNumberRoute)
          .withFormUrlEncodedBody(("value", "exiStiNgREGNum"))

      val result = route(application, postRequest()).value

      status(result) mustEqual BAD_REQUEST
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, index, afaId).url

        FakeRequest(GET, route)
      }

      validateOnIprIndex(
        arbitrary[String],
        IpRightsRegistrationNumberPage.apply,
        getForIndex
      )
    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, index, afaId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "answer"))
      }

      validateOnIprIndex(
        arbitrary[String],
        IpRightsRegistrationNumberPage.apply,
        postForIndex
      )
    }

    "for a GET" must {

      redirectIfLocked(afaId, getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, postRequest)
    }
  }
}
