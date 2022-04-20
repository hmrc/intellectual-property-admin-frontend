/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TechnicalContactInternationalAddressPageSpec extends PageBehaviours {

  "TechnicalContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](TechnicalContactInternationalAddressPage)

    beSettable[InternationalAddress](TechnicalContactInternationalAddressPage)

    beRemovable[InternationalAddress](TechnicalContactInternationalAddressPage)

    "be required if the technical contact is not UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsTechnicalContactUkBasedPage, false).success.value

          TechnicalContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the technical contact is UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsTechnicalContactUkBasedPage, true).success.value

          TechnicalContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if the legal contact is not the technical contact and we don't know if they are UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsTechnicalContactUkBasedPage).success.value

          TechnicalContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
