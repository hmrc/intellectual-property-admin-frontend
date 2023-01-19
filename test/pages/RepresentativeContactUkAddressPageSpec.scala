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

import models.{UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class RepresentativeContactUkAddressPageSpec extends PageBehaviours {

  "RepresentativeContactUkAddressPage" must {

    beRetrievable[UkAddress](RepresentativeContactUkAddressPage)

    beSettable[UkAddress](RepresentativeContactUkAddressPage)

    beRemovable[UkAddress](RepresentativeContactUkAddressPage)

    "be required if the Representative Contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, true).success.value

        RepresentativeContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the Representative Contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, false).success.value

        RepresentativeContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know whether the Representative contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.remove(IsRepresentativeContactUkBasedPage).success.value

        RepresentativeContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
