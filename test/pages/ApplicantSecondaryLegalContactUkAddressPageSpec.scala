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

import models.{UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicantSecondaryLegalContactUkAddressPageSpec extends PageBehaviours {

  "ApplicantSecondaryLegalContactUkAddressPage" must {

    beRetrievable[UkAddress](ApplicantSecondaryLegalContactUkAddressPage)

    beSettable[UkAddress](ApplicantSecondaryLegalContactUkAddressPage)

    beRemovable[UkAddress](ApplicantSecondaryLegalContactUkAddressPage)


    "be required if the secondary legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

          ApplicantSecondaryLegalContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the secondary legal contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantSecondaryLegalContactUkBasedPage, false).success.value

          ApplicantSecondaryLegalContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know if they are UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsApplicantSecondaryLegalContactUkBasedPage).success.value

          ApplicantSecondaryLegalContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
