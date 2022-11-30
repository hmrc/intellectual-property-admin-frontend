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

class AdditionalInfoProvidedPageSpec extends PageBehaviours {

  "AdditionalInfoProvidedPage" must {

    beRetrievable[Boolean](IsExOfficioPage)

    beSettable[Boolean](IsExOfficioPage)

    beRemovable[Boolean](IsExOfficioPage)

    beRequired[Boolean](IsExOfficioPage)

    "remove RestrictedHandlingPage when AdditionalInfoProvided is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[Boolean]]) { (initial, answer) =>
        val answers = answer
          .map { bool =>
            initial.set(RestrictedHandlingPage, bool).success.value
          }
          .getOrElse(initial)

        val result = answers.set(AdditionalInfoProvidedPage, false).success.value

        result.get(RestrictedHandlingPage) mustNot be(defined)
      }
    }
  }
}
