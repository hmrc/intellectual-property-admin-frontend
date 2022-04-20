/*
 * Copyright 2022 HM Revenue & Customs
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
import generators.ModelGenerators
import models.{CompanyApplying, Lock, UserAnswers}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ApplicationReceiptDatePage, CompanyApplyingPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AfaService, LockService}
import viewmodels.DraftRow
import views.html.ViewDraftsView
import org.mockito.Matchers.any

import scala.concurrent.Future

class ViewDraftsControllerSpec extends SpecBase with MockitoSugar with ModelGenerators {

  "ViewDrafts Controller" must {

    "return OK and the correct view for a GET" in {

      val mockAfaService = mock[AfaService]

      val mockLockService = mock[LockService]

      when(mockAfaService.draftList(any())) thenReturn Future.successful(List.empty)
      when(mockLockService.lockList(any())) thenReturn Future.successful(List.empty)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[AfaService].toInstance(mockAfaService),
            bind[LockService].toInstance(mockLockService)
          )
          .build()

      val request = FakeRequest(GET, routes.ViewDraftsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ViewDraftsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(List.empty)(fakeRequest, messages).toString

      application.stop()
    }

    "represent locked and non-locked drafts correctly" in {

      val mockAfaService = mock[AfaService]

      val mockLockService = mock[LockService]

      def emptyUserAnswersWithAfaId(x: Int): UserAnswers =
        UserAnswers(arbitraryAfaId(Some(x)).sample.value)

      val answers1 =
        emptyUserAnswersWithAfaId(11)
          .set(CompanyApplyingPage, CompanyApplying("ipo 1", Some("AN"))).success.value
          .set(ApplicationReceiptDatePage, LocalDate.now).success.value

      val answers2 =
        emptyUserAnswersWithAfaId(22)
          .set(CompanyApplyingPage, CompanyApplying("ipo 2", Some("AN"))).success.value
          .set(ApplicationReceiptDatePage, LocalDate.now).success.value

      val answers3 =
        emptyUserAnswersWithAfaId(33)
          .set(CompanyApplyingPage, CompanyApplying("ipo 3", Some("AN"))).success.value
          .set(ApplicationReceiptDatePage, LocalDate.now).success.value

      val lock1 = Lock(answers1.id, "id", "this user's name")

      val lock2 = Lock(answers2.id, "anotherUserId", "another user's name")

      when(mockAfaService.draftList(any())) thenReturn Future.successful(List(answers1, answers2, answers3))

      when(mockLockService.lockList(any())) thenReturn Future.successful(List(lock1, lock2))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[AfaService].toInstance(mockAfaService),
            bind[LockService].toInstance(mockLockService)
          )
          .build()

      val expectedDraft1 = DraftRow(answers1, isLocked = false)
      val expectedDraft2 = DraftRow(answers2, isLocked = true)
      val expectedDraft3 = DraftRow(answers3, isLocked = false)

      val request = FakeRequest(GET, routes.ViewDraftsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ViewDraftsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(List(expectedDraft1, expectedDraft2, expectedDraft3))(fakeRequest, messages).toString

      application.stop()
    }
  }
}
