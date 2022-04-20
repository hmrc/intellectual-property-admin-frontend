/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SecondaryTechnicalContactUkAddressPageSpec extends PageBehaviours {

  "SecondaryTechnicalContactUkAddressPage" must {

    beRetrievable[UkAddress](SecondaryTechnicalContactUkAddressPage)

    beSettable[UkAddress](SecondaryTechnicalContactUkAddressPage)

    beRemovable[UkAddress](SecondaryTechnicalContactUkAddressPage)


    "be required if the secondary technical contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsSecondaryTechnicalContactUkBasedPage, true).success.value

          SecondaryTechnicalContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the secondary technical contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsSecondaryTechnicalContactUkBasedPage, false).success.value

          SecondaryTechnicalContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know if they are UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsSecondaryTechnicalContactUkBasedPage).success.value

          SecondaryTechnicalContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
