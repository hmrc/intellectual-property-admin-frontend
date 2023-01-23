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

import models.{InternationalAddress, TechnicalContact, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddAnotherTechnicalContactPageSpec extends PageBehaviours {

  "AddAnotherTechnicalContactPage" must {

    beRetrievable[Boolean](AddAnotherTechnicalContactPage)

    beSettable[Boolean](AddAnotherTechnicalContactPage)

    beRemovable[Boolean](AddAnotherTechnicalContactPage)

    beRequired[Boolean](AddAnotherTechnicalContactPage)
  }

  "remove all secondary technical contact data when the answer is no" in {

    forAll(
      arbitrary[UserAnswers],
      arbitrary[TechnicalContact],
      arbitrary[Boolean],
      arbitrary[UkAddress],
      arbitrary[InternationalAddress]
    ) { (userAnswers, secondaryLegalContact, ukBased, ukAddress, internationalAddress) =>
      val answers =
        userAnswers
          .set(WhoIsSecondaryTechnicalContactPage, secondaryLegalContact)
          .success
          .value
          .set(IsSecondaryTechnicalContactUkBasedPage, ukBased)
          .success
          .value
          .set(SecondaryTechnicalContactUkAddressPage, ukAddress)
          .success
          .value
          .set(SecondaryTechnicalContactInternationalAddressPage, internationalAddress)
          .success
          .value

      val result = answers.set(AddAnotherTechnicalContactPage, false).success.value

      result.get(WhoIsSecondaryTechnicalContactPage)                must not be defined
      result.get(IsSecondaryTechnicalContactUkBasedPage)            must not be defined
      result.get(SecondaryTechnicalContactUkAddressPage)            must not be defined
      result.get(SecondaryTechnicalContactInternationalAddressPage) must not be defined
    }
  }

  "not remove the secondary technical contact data when the answer is yes" in {

    forAll(
      arbitrary[UserAnswers],
      arbitrary[TechnicalContact],
      arbitrary[Boolean],
      arbitrary[UkAddress],
      arbitrary[InternationalAddress]
    ) { (userAnswers, secondaryLegalContact, ukBased, ukAddress, internationalAddress) =>
      val answers =
        userAnswers
          .set(WhoIsSecondaryTechnicalContactPage, secondaryLegalContact)
          .success
          .value
          .set(IsSecondaryTechnicalContactUkBasedPage, ukBased)
          .success
          .value
          .set(SecondaryTechnicalContactUkAddressPage, ukAddress)
          .success
          .value
          .set(SecondaryTechnicalContactInternationalAddressPage, internationalAddress)
          .success
          .value

      val result = answers.set(AddAnotherTechnicalContactPage, true).success.value

      result.get(WhoIsSecondaryTechnicalContactPage) mustBe defined
      result.get(IsSecondaryTechnicalContactUkBasedPage) mustBe defined
      result.get(SecondaryTechnicalContactUkAddressPage) mustBe defined
      result.get(SecondaryTechnicalContactInternationalAddressPage) mustBe defined
    }
  }
}
