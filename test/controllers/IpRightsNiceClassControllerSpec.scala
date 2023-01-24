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
import forms.IpRightsNiceClassFormProvider
import models.{AfaId, NiceClassId, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.IpRightsNiceClassPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import views.html.IpRightsNiceClassView

import scala.concurrent.Future

class IpRightsNiceClassControllerSpec extends SpecBase with MockitoSugar with LockAfaChecks with IprIndexValidation {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val iprIndex = 0

  val niceClassIndex = 0

  val formProvider = new IpRightsNiceClassFormProvider()
  private def form = formProvider(Seq.empty)

  lazy val ipRightsNiceClassRoute: String =
    routes.IpRightsNiceClassController.onPageLoad(NormalMode, iprIndex, niceClassIndex, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  def getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ipRightsNiceClassRoute)

  def postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, ipRightsNiceClassRoute)
      .withFormUrlEncodedBody(("value", "1"))

  val niceClassId: NiceClassId = NiceClassId.fromInt(1).value

  "IpRightsNiceClass Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[IpRightsNiceClassView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, iprIndex, niceClassIndex, afaId)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(IpRightsNiceClassPage(iprIndex, niceClassIndex), niceClassId).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[IpRightsNiceClassView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(niceClassId), NormalMode, iprIndex, niceClassIndex, afaId)(getRequest, messages).toString

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
        FakeRequest(POST, ipRightsNiceClassRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IpRightsNiceClassView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, iprIndex, niceClassIndex, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK for a GET when other NICE classes already exist" in {

      val answers =
        emptyUserAnswers
          .set(IpRightsNiceClassPage(0, 0), NiceClassId.fromInt(1).value)
          .success
          .value
          .set(IpRightsNiceClassPage(0, 1), NiceClassId.fromInt(2).value)
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

      val getRoute = routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 2, afaId).url

      val request = FakeRequest(GET, getRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
    }

    "not allow a duplicate value to be resubmitted" in {

      val existingNiceClass = 2

      val answers =
        emptyUserAnswers
          .set(IpRightsNiceClassPage(0, 0), NiceClassId.fromInt(1).value)
          .success
          .value
          .set(IpRightsNiceClassPage(0, 1), NiceClassId.fromInt(existingNiceClass).value)
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

      val postRoute = routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, afaId).url

      val request = FakeRequest(POST, postRoute)
        .withFormUrlEncodedBody(("value", existingNiceClass.toString))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "allow the same value to be resubmitted" in {

      val answers =
        emptyUserAnswers
          .set(IpRightsNiceClassPage(0, 0), NiceClassId.fromInt(1).value)
          .success
          .value
          .set(IpRightsNiceClassPage(0, 1), NiceClassId.fromInt(2).value)
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

      val postRoute = routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, afaId).url

      val request = FakeRequest(POST, postRoute)
        .withFormUrlEncodedBody(("value", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
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

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.IpRightsNiceClassController.onPageLoad(NormalMode, index, 0, afaId).url

        FakeRequest(GET, route)
      }

      validateOnIprIndex(
        arbitrary[NiceClassId],
        IpRightsNiceClassPage.apply(_, 0),
        getForIndex
      )

      "return not found if a given Nice Class index is out of bounds" in {

        val gen = for {
          answers <- Gen.listOf(arbitrary[NiceClassId]).map(_.zipWithIndex)
          index   <- Gen.oneOf(
                       Gen.chooseNum(answers.size + 1, answers.size + 100),
                       Gen.chooseNum(-100, -1)
                     )
        } yield (answers, index)

        def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
          val route = routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, index, afaId).url

          FakeRequest(GET, route)
        }

        forAll(gen, arbitrary[AfaId]) { case ((answers, index), afaId) =>
          val userAnswers = answers.foldLeft(UserAnswers(afaId)) { case (userAnswers, (answer, index)) =>
            userAnswers.set(IpRightsNiceClassPage(0, index), answer).success.value
          }

          val mockAfaService = mock[AfaService]

          when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

          val application =
            applicationBuilder(Some(userAnswers))
              .overrides(bind[AfaService].toInstance(mockAfaService))
              .build()

          val result = route(application, getForIndex(index)).value

          status(result) mustEqual NOT_FOUND

          application.stop()
        }
      }

      redirectIfLocked(afaId, () => getRequest)
    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.IpRightsNiceClassController.onPageLoad(NormalMode, index, 0, afaId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "answer"))
      }

      validateOnIprIndex(
        arbitrary[NiceClassId],
        IpRightsNiceClassPage.apply(_, 0),
        postForIndex
      )

      "return not found if a given Nice Class index is out of bounds" in {

        val gen = for {
          answers <- Gen.listOf(arbitrary[NiceClassId]).map(_.zipWithIndex)
          index   <- Gen.oneOf(
                       Gen.chooseNum(answers.size + 1, answers.size + 100),
                       Gen.chooseNum(-100, -1)
                     )
        } yield (answers, index)

        def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

          val route =
            routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, index, afaId).url

          FakeRequest(POST, route)
            .withFormUrlEncodedBody(("value", "answer"))
        }

        forAll(gen, arbitrary[AfaId]) { case ((answers, index), afaId) =>
          val userAnswers = answers.foldLeft(UserAnswers(afaId)) { case (userAnswers, (answer, index)) =>
            userAnswers.set(IpRightsNiceClassPage(0, index), answer).success.value
          }

          val mockAfaService = mock[AfaService]

          when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

          val application =
            applicationBuilder(Some(userAnswers))
              .overrides(bind[AfaService].toInstance(mockAfaService))
              .build()

          val result = route(application, postForIndex(index)).value

          status(result) mustEqual NOT_FOUND

          application.stop()
        }
      }

      redirectIfLocked(afaId, () => postRequest)
    }
  }
}
