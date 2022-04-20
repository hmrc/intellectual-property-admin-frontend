/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicantSecondaryLegalContactInternationalAddressPageSpec extends PageBehaviours {

  "ApplicantSecondaryLegalContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](ApplicantSecondaryLegalContactInternationalAddressPage)

    beSettable[InternationalAddress](ApplicantSecondaryLegalContactInternationalAddressPage)

    beRemovable[InternationalAddress](ApplicantSecondaryLegalContactInternationalAddressPage)


    "not be required if the secondary legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

          ApplicantSecondaryLegalContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }


    "be required if the secondary legal contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantSecondaryLegalContactUkBasedPage, false).success.value

          ApplicantSecondaryLegalContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not know whether it's required if we don't know if they are UK based" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsApplicantSecondaryLegalContactUkBasedPage).success.value

          ApplicantSecondaryLegalContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
