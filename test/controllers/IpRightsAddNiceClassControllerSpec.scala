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
import generators.Generators
import models.{AfaId, NiceClassId, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.{IpRightsAddNiceClassPage, IpRightsNiceClassPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import utils.ReviewHelper
import views.html.IpRightsAddNiceClassView

import scala.concurrent.Future

class IpRightsAddNiceClassControllerSpec extends SpecBase with IprIndexValidation with Generators with LockAfaChecks {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId   = userAnswersId
  val iprIndex       = 0
  val niceClassIndex = 0

  lazy val ipRightsAddNiceClassRoute: String    =
    routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, 0, afaId).url
  lazy val ipRightsDeleteNiceClassRoute: String =
    routes.IpRightsAddNiceClassController.onDelete(NormalMode, iprIndex, niceClassIndex, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ipRightsAddNiceClassRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, ipRightsAddNiceClassRoute)
      .withFormUrlEncodedBody(("value", "true"))

  "IpRightsAddNiceClass Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers =
        UserAnswers(afaId)
          .set(IpRightsNiceClassPage(iprIndex, 0), NiceClassId.fromInt(1).value)
          .success
          .value

      val cyaHelper = new ReviewHelper(userAnswers)

      val expectedSections = cyaHelper.niceClassReviewRow(NormalMode, iprIndex).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[IpRightsAddNiceClassView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          NormalMode,
          iprIndex,
          afaId,
          expectedSections,
          routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 1, afaId).url
        )(messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect on delete" in {

      val niceClass1 = NiceClassId.fromInt(1).value
      val niceClass2 = NiceClassId.fromInt(2).value

      val userAnswers = UserAnswers(userAnswersId)
        .set(IpRightsNiceClassPage(iprIndex, niceClassIndex), niceClass1)
        .success
        .value
        .set(IpRightsNiceClassPage(iprIndex, niceClassIndex + 1), niceClass2)
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

      val request = FakeRequest(GET, ipRightsDeleteNiceClassRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      application.stop()
    }
  }

  "for a GET" must {

    def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
      val route = routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, index, afaId).url

      FakeRequest(GET, route)
    }

    validateOnIprIndex(
      arbitrary[Boolean],
      IpRightsAddNiceClassPage.apply,
      getForIndex
    )
  }

  "for a GET" must {

    redirectIfLocked(afaId, () => getRequest)
  }
}
