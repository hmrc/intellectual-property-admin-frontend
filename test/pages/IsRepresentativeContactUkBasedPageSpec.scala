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

package pages

import models.{InternationalAddress, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsRepresentativeContactUkBasedPageSpec extends PageBehaviours {

  "IsRepresentativeContactUkBasedPage" must {

    beRetrievable[Boolean](IsRepresentativeContactUkBasedPage)

    beSettable[Boolean](IsRepresentativeContactUkBasedPage)

    beRemovable[Boolean](IsRepresentativeContactUkBasedPage)

    beRequired[Boolean](IsRepresentativeContactUkBasedPage)

    "remove RepresentativeContactUkAddress when IsRepresentativeContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(RepresentativeContactUkAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsRepresentativeContactUkBasedPage, false).success.value

        result.get(RepresentativeContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove RepresentativeContactInternationalAddress when IsRepresentativeContactUkBased is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(RepresentativeContactInternationalAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsRepresentativeContactUkBasedPage, true).success.value

        result.get(RepresentativeContactInternationalAddressPage) mustNot be(defined)
      }
    }
  }
}
