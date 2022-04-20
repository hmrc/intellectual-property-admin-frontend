/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicantLegalContactUkAddressPageSpec extends PageBehaviours {

  "RightsHolderUkAddressPage" must {

    beRetrievable[UkAddress](ApplicantLegalContactUkAddressPage)

    beSettable[UkAddress](ApplicantLegalContactUkAddressPage)

    beRemovable[UkAddress](ApplicantLegalContactUkAddressPage)

    "be required if the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantLegalContactUkBasedPage, true).success.value

          ApplicantLegalContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the legal contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantLegalContactUkBasedPage, false).success.value

          ApplicantLegalContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know whether the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsApplicantLegalContactUkBasedPage).success.value

          ApplicantLegalContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
