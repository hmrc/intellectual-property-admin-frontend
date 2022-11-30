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

import models.{InternationalAddress, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsSecondaryTechnicalContactUkBasedPageSpec extends PageBehaviours {

  "IsSecondaryTechnicalContactUkBasedPage" must {

    beRetrievable[Boolean](IsSecondaryTechnicalContactUkBasedPage)

    beSettable[Boolean](IsSecondaryTechnicalContactUkBasedPage)

    beRemovable[Boolean](IsSecondaryTechnicalContactUkBasedPage)

    "be required if the add another technical contact is set to true" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(AddAnotherTechnicalContactPage, true)
            .success
            .value

        IsSecondaryTechnicalContactUkBasedPage.isRequired(answers).value mustEqual true
      }
    }

    "NOT be required if the add another technical contact is set to false" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(AddAnotherTechnicalContactPage, false)
            .success
            .value

        IsSecondaryTechnicalContactUkBasedPage.isRequired(answers).value mustEqual false
      }
    }

    "remove SecondaryTechnicalContactUkAddress when IsSecondaryTechnicalContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(SecondaryTechnicalContactUkAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsSecondaryTechnicalContactUkBasedPage, false).success.value

        result.get(SecondaryTechnicalContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove SecondaryTechnicalContactInternationalAddress when IsSecondaryTechnicalContactUkBased is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(SecondaryTechnicalContactInternationalAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsSecondaryTechnicalContactUkBasedPage, true).success.value

        result.get(SecondaryTechnicalContactInternationalAddressPage) mustNot be(defined)
      }
    }

    "remove SecondaryTechnicalContactUkAddressPage when IsSecondaryTechnicalContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(SecondaryTechnicalContactUkAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsSecondaryTechnicalContactUkBasedPage, false).success.value

        result.get(SecondaryTechnicalContactUkAddressPage) mustNot be(defined)
      }
    }

  }
}
