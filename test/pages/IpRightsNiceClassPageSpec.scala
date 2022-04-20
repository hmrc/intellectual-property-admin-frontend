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

import models.{IpRightsType, NiceClassId, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class IpRightsNiceClassPageSpec extends PageBehaviours {

  "IpRightsNiceClassPage" must {

    beRetrievable[NiceClassId](IpRightsNiceClassPage(0, 0))

    beSettable[NiceClassId](IpRightsNiceClassPage(0, 0))

    beRemovable[NiceClassId](IpRightsNiceClassPage(0, 0))

    "be required for index 0 if this right is a Trademark" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

          IpRightsNiceClassPage(0, 0).isRequired(answers).value mustEqual true
      }
    }

    "not be required for index 0 if this right is not a Trademark" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.Trademark) {

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            IpRightsNiceClassPage(0, 0).isRequired(answers).value mustEqual false
          }
      }
    }

    "not be required for indexes above 0" in {

      val arbitrarilyHighIndex = 1000

      forAll(arbitrary[UserAnswers], Gen.choose(1, arbitrarilyHighIndex)) {
        (userAnswers, index) =>

          IpRightsNiceClassPage(0, index).isRequired(userAnswers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsNiceClassPage(0, 0).isRequired(answers) must not be defined
      }
    }
  }
}
