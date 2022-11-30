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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class WhoIsSecondaryLegalContactPageSpec extends PageBehaviours {

  "WhoIsSecondaryLegalContactPageSpec" must {

    beRetrievable[WhoIsSecondaryLegalContactPageSpec]

    beSettable[WhoIsSecondaryLegalContactPageSpec]

    beRemovable[WhoIsSecondaryLegalContactPageSpec]

    "be required if add another legal contact is true" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(AddAnotherLegalContactPage, true).success.value

        WhoIsSecondaryLegalContactPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if add another legal contact is false" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.set(AddAnotherLegalContactPage, false).success.value

        WhoIsSecondaryLegalContactPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know whether to add another legal contact" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.remove(AddAnotherLegalContactPage).success.value

        WhoIsSecondaryLegalContactPage.isRequired(answers) must not be defined
      }
    }
  }
}
