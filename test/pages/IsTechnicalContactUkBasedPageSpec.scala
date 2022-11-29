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

class IsTechnicalContactUkBasedPageSpec extends PageBehaviours {

  "IsTechnicalContactUkBasedPage" must {

    beRetrievable[Boolean](IsTechnicalContactUkBasedPage)

    beSettable[Boolean](IsTechnicalContactUkBasedPage)

    beRemovable[Boolean](IsTechnicalContactUkBasedPage)

    beRequired[Boolean](IsTechnicalContactUkBasedPage)

    "remove TechnicalContactUkAddress when IsTechnicalContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(TechnicalContactUkAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsTechnicalContactUkBasedPage, false).success.value

        result.get(TechnicalContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove TechnicalContactInternationalAddress when IsInfringementsContactUkBased is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(TechnicalContactInternationalAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsTechnicalContactUkBasedPage, true).success.value

        result.get(TechnicalContactInternationalAddressPage) mustNot be(defined)
      }
    }
  }
}
