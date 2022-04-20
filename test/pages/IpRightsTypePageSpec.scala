/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class IpRightsTypePageSpec extends PageBehaviours {

  "IpRightsTypePage" must {

    beRetrievable[IpRightsType](IpRightsTypePage(0))

    beSettable[IpRightsType](IpRightsTypePage(0))

    beRemovable[IpRightsType](IpRightsTypePage(0))

    "remove IP Rights Registration Number, Registration End, Description With Brand, and Nice classes " +
      "when the type is Copyright, Plant variety, Geographical Indication or Semiconductor topology" in {

      import IpRightsType._

      val newTypeGen = Gen.oneOf(Copyright, PlantVariety, GeographicalIndication, SemiconductorTopography)

      forAll(arbitrary[UserAnswers], newTypeGen) {
        (userAnswers, rightsType) =>

          val result = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          result.get(IpRightsRegistrationNumberPage(0)) must not be defined
          result.get(IpRightsRegistrationEndPage(0)) must not be defined
          result.get(IpRightsDescriptionWithBrandPage(0)) must not be defined
          result.get(IpRightsNiceClassPage(0,0)) must not be defined
      }
    }

    "remove Supplementary Protection Certificate Type when the type is changed to anything other than Supplementary Protection Certificate" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.SupplementaryProtectionCertificate) {

            val result = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            result.get(IpRightsSupplementaryProtectionCertificateTypePage(0)) must not be defined
          }
      }
    }

    "remove Description With Brand, and Nice classes when the type is changed to Design, Patent or Supplementary Protection Certificate" in {

      val typeGen = Gen.oneOf(IpRightsType.Design, IpRightsType.Patent, IpRightsType.SupplementaryProtectionCertificate)

      forAll(arbitrary[UserAnswers], typeGen) {
        (userAnswers, rightsType) =>

          val result = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          result.get(IpRightsDescriptionWithBrandPage(0)) must not be defined
          result.get(IpRightsNiceClassPage(0, 0)) must not be defined
      }
    }

    "be required for index 0" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          IpRightsTypePage(0).isRequired(userAnswers).value mustEqual true
      }
    }

    "not be required for indexes greater than 0" in {

      val arbitrarilyHighIndex = 1000

      forAll(arbitrary[UserAnswers], Gen.chooseNum(1, arbitrarilyHighIndex)) {
        (userAnswers, index) =>

          IpRightsTypePage(index).isRequired(userAnswers).value mustEqual false
      }
    }
  }
}
