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
import models.{AfaId, IpRightsType, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.IpRightsTypePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import utils.ReviewHelper
import views.html.AddIpRightView

import scala.concurrent.Future

class AddIpRightControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  lazy val addIpRightRoute: String = routes.AddIpRightController.onPageLoad(NormalMode, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  val baseAnswers: UserAnswers = UserAnswers(afaId).set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

  def addIpRightsUrl(nextIpRightIndex: Int): String =
    routes.IpRightsTypeController.onPageLoad(NormalMode, nextIpRightIndex, afaId).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, addIpRightRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, addIpRightRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "AddIpRight Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val helper = new ReviewHelper(baseAnswers)

      val expectedSections = helper.iprReviewRow(NormalMode)

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[AddIpRightView]

      val nextPage = routes.AdditionalInfoProvidedController.onPageLoad(NormalMode, afaId)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(NormalMode, afaId, expectedSections, addIpRightsUrl(1), numberOfIpRights = 1, nextPage)(messages).toString

      application.stop()
    }

    "display the correct number of IP rights on the page" in {

      val application = applicationBuilder(
        Some(
          baseAnswers
            .set(IpRightsTypePage(0), IpRightsType.Trademark)
            .success
            .value
            .set(IpRightsTypePage(1), IpRightsType.Trademark)
            .success
            .value
            .set(IpRightsTypePage(2), IpRightsType.Trademark)
            .success
            .value
        )
      ).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[AddIpRightView]

      val expectUA = UserAnswers(afaId)
        .set(IpRightsTypePage(0), IpRightsType.Trademark)
        .success
        .value
        .set(IpRightsTypePage(1), IpRightsType.Trademark)
        .success
        .value
        .set(IpRightsTypePage(2), IpRightsType.Trademark)
        .success
        .value

      val expectedSections = new ReviewHelper(expectUA).iprReviewRow(NormalMode)

      val nextPage = routes.AdditionalInfoProvidedController.onPageLoad(NormalMode, afaId)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(NormalMode, afaId, expectedSections, addIpRightsUrl(3), expectedSections.toOption.get.size, nextPage)(
          messages
        ).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to the next page for a DELETE on a valid index" in {

      val userAnswers =
        UserAnswers(afaId)
          .set(IpRightsTypePage(0), IpRightsType.Trademark)
          .success
          .value

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val request = FakeRequest(GET, routes.AddIpRightController.onDelete(NormalMode, afaId, 0).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      application.stop()
    }

    "return NOT FOUND for a DELETE on an invalid index" in {

      val userAnswers =
        UserAnswers(afaId)
          .set(IpRightsTypePage(0), IpRightsType.Trademark)
          .success
          .value

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val request = FakeRequest(GET, routes.AddIpRightController.onDelete(NormalMode, afaId, 2).url)

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }

    "for a GET" must {

      redirectIfLocked(afaId, getRequest)
    }
  }
}
