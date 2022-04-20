/*
 * Copyright 2022 HM Revenue & Customs
 *
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

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value

          IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers).value mustEqual true
      }
    }

    "not be required if this right is not a supplementary protection certificate" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.SupplementaryProtectionCertificate) {

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers).value mustEqual false
          }
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsSupplementaryProtectionCertificateTypePage(0).isRequired(answers) must not be defined
      }
    }
  }
}
