/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicantLegalContactInternationalAddressPageSpec extends PageBehaviours {

  "RightsHolderContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    beSettable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    beRemovable[InternationalAddress](ApplicantLegalContactInternationalAddressPage)

    "be required if the Legal contact is not UK based" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantLegalContactUkBasedPage, false).success.value

          ApplicantLegalContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }


    "not be required if the legal contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IsApplicantLegalContactUkBasedPage, true).success.value

          ApplicantLegalContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if the representative contact is not the legal contact and we don't know if they are UK based" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(IsApplicantLegalContactUkBasedPage).success.value

          ApplicantLegalContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
