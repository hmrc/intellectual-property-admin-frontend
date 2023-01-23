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

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicantLegalContactInternationalAddressPageSpec extends PageBehaviours {

  "RightsHolderContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    beSettable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    beRemovable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    "be required if the Legal contact is not UK based" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(IsApplicantLegalContactUkBasedPage, false)
            .success
            .value

        ApplicantLegalContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(IsApplicantLegalContactUkBasedPage, true)
            .success
            .value

        ApplicantLegalContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if the representative contact is not the legal contact and we don't know if they are UK based" in {
      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .remove(IsApplicantLegalContactUkBasedPage)
            .success
            .value

        ApplicantLegalContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
