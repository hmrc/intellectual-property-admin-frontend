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

class ApplicantLegalContactUkAddressPageSpec extends PageBehaviours {

  "RightsHolderUkAddressPage" must {

    beRetrievable[UkAddress](ApplicantLegalContactUkAddressPage)

    beSettable[UkAddress](ApplicantLegalContactUkAddressPage)

    beRemovable[UkAddress](ApplicantLegalContactUkAddressPage)

    "be required if the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(IsApplicantLegalContactUkBasedPage, true)
            .success
            .value

        ApplicantLegalContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the legal contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(IsApplicantLegalContactUkBasedPage, false)
            .success
            .value

        ApplicantLegalContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know whether the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .remove(IsApplicantLegalContactUkBasedPage)
            .success
            .value

        ApplicantLegalContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
