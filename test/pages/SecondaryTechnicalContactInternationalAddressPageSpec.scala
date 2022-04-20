/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SecondaryTechnicalContactInternationalAddressPageSpec extends PageBehaviours {

  "SecondaryTechnicalContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](SecondaryTechnicalContactInternationalAddressPage)

    beSettable[InternationalAddress](SecondaryTechnicalContactInternationalAddressPage)

    beRemovable[InternationalAddress](SecondaryTechnicalContactInternationalAddressPage)


    "not be required if the secondary technical contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsSecondaryTechnicalContactUkBasedPage, true).success.value

          SecondaryTechnicalContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }


    "be required if the secondary technical contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsSecondaryTechnicalContactUkBasedPage, false).success.value

          SecondaryTechnicalContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not know whether it's required if we don't know if they are UK based" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsSecondaryTechnicalContactUkBasedPage).success.value

          SecondaryTechnicalContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
