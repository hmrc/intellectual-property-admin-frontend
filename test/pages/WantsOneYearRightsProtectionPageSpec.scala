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

class WantsOneYearRightsProtectionPageSpec extends PageBehaviours {

  "WantsOneYearRightsProtectionPage" must {

    beRetrievable[Boolean](WantsOneYearRightsProtectionPage)

    beSettable[Boolean](WantsOneYearRightsProtectionPage)

    beRemovable[Boolean](WantsOneYearRightsProtectionPage)

    "be required if this application is ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsExOfficioPage, true).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if this application is not ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsExOfficioPage, false).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know if this application is ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IsExOfficioPage).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers) must not be defined
      }
    }
  }
}
