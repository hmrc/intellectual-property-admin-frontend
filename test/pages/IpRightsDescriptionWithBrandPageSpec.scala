/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsDescriptionWithBrand, IpRightsType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IpRightsDescriptionWithBrandPageSpec extends PageBehaviours {

  "IpRightsDescriptionWithBrandPage" must {

    beRetrievable[IpRightsDescriptionWithBrand](IpRightsDescriptionWithBrandPage(0))

    beSettable[IpRightsDescriptionWithBrand](IpRightsDescriptionWithBrandPage(0))

    beRemovable[IpRightsDescriptionWithBrand](IpRightsDescriptionWithBrandPage(0))

    "be required if this right is a Trademark" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

          IpRightsDescriptionWithBrandPage(0).isRequired(answers).value mustEqual true
      }
    }

    "not be required if this right is not a Trademark" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.Trademark) {

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            IpRightsDescriptionWithBrandPage(0).isRequired(answers).value mustEqual false
          }
      }
    }
  }
}
