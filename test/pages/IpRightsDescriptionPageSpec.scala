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

import models.{IpRightsType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IpRightsDescriptionPageSpec extends PageBehaviours {

  "IpRightsDescriptionPage" must {

    beRetrievable[String](IpRightsDescriptionPage(0))

    beSettable[String](IpRightsDescriptionPage(0))

    beRemovable[String](IpRightsDescriptionPage(0))

    "be required if this right is not a Trademark" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) { (userAnswers, rightsType) =>
        whenever(rightsType != IpRightsType.Trademark) {

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsDescriptionPage(0).isRequired(answers).value mustEqual true
        }
      }
    }

    "not be required if this right is a Trademark" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers
            .set(IpRightsTypePage(0), IpRightsType.Trademark)
            .success
            .value

        IpRightsDescriptionPage(0).isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

        IpRightsDescriptionPage(0).isRequired(answers) must not be defined
      }
    }
  }
}
