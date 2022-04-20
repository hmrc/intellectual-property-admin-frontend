/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.UserAnswers
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IpRightsRegistrationEndPageSpec extends PageBehaviours {

  "IpRightsRegistrationEndPage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](IpRightsRegistrationEndPage(0))

    beSettable[LocalDate](IpRightsRegistrationEndPage(0))

    beRemovable[LocalDate](IpRightsRegistrationEndPage(0))

    "be required if this is a Trademark, Design, Patent or Supplementary Protection Certificate" in {

      import models.IpRightsType._

      val rightsTypeGen = Gen.oneOf(Trademark, Design, Patent, SupplementaryProtectionCertificate)

      forAll(arbitrary[UserAnswers], rightsTypeGen) {
        (userAnswers, rightsType) =>

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsRegistrationEndPage(0).isRequired(answers).value mustEqual true
      }
    }

    "not be required if this is not a Trademark, Design, Patent or Supplementary Protection Certificate" in {

      import models.IpRightsType._

      val rightsTypeGen = Gen.oneOf(PlantVariety, GeographicalIndication, Copyright, SemiconductorTopography)

      forAll(arbitrary[UserAnswers], rightsTypeGen) {
        (userAnswers, rightsType) =>

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsRegistrationEndPage(0).isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we do not know what type of right it is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsRegistrationEndPage(0).isRequired(answers) must not be defined
      }
    }
  }
}
