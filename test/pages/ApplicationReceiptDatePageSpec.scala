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

package pages

import java.time.LocalDate

import models.UserAnswers
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicationReceiptDatePageSpec extends PageBehaviours {

  "ApplicationReceiptDatePage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2018, 1, 1))
    }

    beRetrievable[LocalDate](ApplicationReceiptDatePage)

    beSettable[LocalDate](ApplicationReceiptDatePage)

    beRemovable[LocalDate](ApplicationReceiptDatePage)

    "be required if this application is not migrated" in {

      forAll(arbitrary[UserAnswers], arbitraryUkAfaId(None)) { (userAnswers, afaId) =>
        val answers = userAnswers.copy(id = afaId)

        ApplicationReceiptDatePage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if this application is migrated" in {

      forAll(arbitrary[UserAnswers], arbitraryGbAfaId(None)) { (userAnswers, afaId) =>
        val answers = userAnswers.copy(id = afaId)

        ApplicationReceiptDatePage.isRequired(answers).value mustEqual false
      }
    }
  }
}
