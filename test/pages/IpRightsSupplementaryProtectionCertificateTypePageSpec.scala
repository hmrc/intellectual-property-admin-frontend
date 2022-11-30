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

import models.{IpRightsSupplementaryProtectionCertificateType, IpRightsType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IpRightsSupplementaryProtectionCertificateTypePageSpec extends PageBehaviours {

  "IpRightsSupplementaryProtectionCertificateTypePage" must {

    beRetrievable[IpRightsSupplementaryProtectionCertificateType](IpRightsSupplementaryProtectionCertificateTypePage(0))

    beSettable[IpRightsSupplementaryProtectionCertificateType](IpRightsSupplementaryProtectionCertificateTypePage(0))

    beRemovable[IpRightsSupplementaryProtectionCertificateType](IpRightsSupplementaryProtectionCertificateTypePage(0))

    "be required if this right is a supplementary protection certificate" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers =
          userAnswers.set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value

        IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers).value mustEqual true
      }
    }

    "not be required if this right is not a supplementary protection certificate" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) { (userAnswers, rightsType) =>
        whenever(rightsType != IpRightsType.SupplementaryProtectionCertificate) {

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers).value mustEqual false
        }
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) { userAnswers =>
        val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

        IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers) must not be defined
      }
    }
  }
}
