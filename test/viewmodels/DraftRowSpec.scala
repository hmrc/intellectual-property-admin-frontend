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

package viewmodels

import generators.Generators
import models.{CompanyApplying, Region, UserAnswers}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ApplicationReceiptDatePage, CompanyApplyingPage}
import queries.PublicationDeadlineQuery
import services.WorkingDaysService

import java.time.LocalDate
import scala.concurrent.Future

class DraftRowSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators with MockitoSugar {

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] =
    Arbitrary(datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(2100, 1, 1)))

  "a draft row" - {

    "must be created from a user answers" in {

      val arbitraryFutureDate = LocalDate.of(2100, 2, 2)

      val mockWorkingDaysService = mock[WorkingDaysService]
      when(mockWorkingDaysService.workingDays(eqTo(Region.EnglandAndWales), any(), any())(any(), any())) thenReturn Future.successful(arbitraryFutureDate)

      forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[LocalDate], arbitrary[Boolean]) {
        (initialAnswers, companyName, date, isLocked) =>

          val answers = initialAnswers
            .set(CompanyApplyingPage, CompanyApplying(companyName, None)).success.value
            .set(ApplicationReceiptDatePage, date).success.value
            .set(PublicationDeadlineQuery, arbitraryFutureDate).success.value

          DraftRow(answers, isLocked) mustEqual DraftRow(Some(companyName), answers.id, Some(arbitraryFutureDate), isLocked)
      }
    }
  }
}
