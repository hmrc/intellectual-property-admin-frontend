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

class IsApplicantLegalContactUkBasedPageSpec extends PageBehaviours {

  "IsApplicantLegalContactUkBasedPage" must {

    beRetrievable[Boolean](IsApplicantLegalContactUkBasedPage)

    beSettable[Boolean](IsApplicantLegalContactUkBasedPage)

    beRemovable[Boolean](IsApplicantLegalContactUkBasedPage)

    beRequired[Boolean](IsApplicantLegalContactUkBasedPage)

    "remove LegalContactUkAddress when IsApplicantLegalContactUkBasedPage is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(ApplicantLegalContactUkAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsApplicantLegalContactUkBasedPage, false).success.value

        result.get(ApplicantLegalContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove LegalContactInternationalAddress when IsApplicantLegalContactUkBasedPage is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) { (initial, answer) =>
        val answers = answer
          .map { address =>
            initial.set(ApplicantLegalContactInternationalAddressPage, address).success.value
          }
          .getOrElse(initial)

        val result = answers.set(IsApplicantLegalContactUkBasedPage, true).success.value

        result.get(ApplicantLegalContactInternationalAddressPage) mustNot be(defined)
      }
    }
  }
}
