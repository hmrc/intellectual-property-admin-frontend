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

class IsApplicantSecondaryLegalContactUkBasedPageSpec extends PageBehaviours {

  "IsApplicantSecondaryLegalContactUkBasedPage" must {

    beRetrievable[Boolean](IsApplicantSecondaryLegalContactUkBasedPage)

    beSettable[Boolean](IsApplicantSecondaryLegalContactUkBasedPage)

    beRemovable[Boolean](IsApplicantSecondaryLegalContactUkBasedPage)

    "be required if the add another legal contact is set to true" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(AddAnotherLegalContactPage, true).success.value

          IsApplicantSecondaryLegalContactUkBasedPage.isRequired(answers).value mustEqual true
      }
    }

    "NOT be required if the add another legal contact is set to false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(AddAnotherLegalContactPage, false).success.value

          IsApplicantSecondaryLegalContactUkBasedPage.isRequired(answers).value mustEqual false
      }
    }

    "remove ApplicantSecondaryLegalContactUkAddress when IsApplicantSecondaryLegalContactUkBasedPage is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(ApplicantSecondaryLegalContactUkAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsApplicantSecondaryLegalContactUkBasedPage, false).success.value

          result.get(ApplicantSecondaryLegalContactUkAddressPage) mustNot be (defined)
      }
    }

    "remove ApplicantSecondaryLegalContactInternationalAddress when IsApplicantSecondaryLegalContactUkBasedPage is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(ApplicantSecondaryLegalContactInternationalAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

          result.get(ApplicantSecondaryLegalContactInternationalAddressPage) mustNot be (defined)
      }
    }

    "remove ApplicantSecondaryLegalContactUkAddressPage when IsApplicantSecondaryLegalContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(ApplicantSecondaryLegalContactUkAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsApplicantSecondaryLegalContactUkBasedPage, false).success.value

          result.get(ApplicantSecondaryLegalContactUkAddressPage) mustNot be (defined)
      }
    }

  }
}
