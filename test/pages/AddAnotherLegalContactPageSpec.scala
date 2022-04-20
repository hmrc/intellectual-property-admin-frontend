/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UkAddress, UserAnswers, WhoIsSecondaryLegalContact}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddAnotherLegalContactPageSpec extends PageBehaviours {

  "AddAnotherLegalContactPage" must {

    beRetrievable[Boolean](AddAnotherLegalContactPage)

    beSettable[Boolean](AddAnotherLegalContactPage)

    beRemovable[Boolean](AddAnotherLegalContactPage)

    beRequired[Boolean](AddAnotherLegalContactPage)
  }

  "remove all secondary legal contact data when the answer is no" in {

    forAll(arbitrary[UserAnswers], arbitrary[WhoIsSecondaryLegalContact], arbitrary[Boolean], arbitrary[UkAddress], arbitrary[InternationalAddress]) {
      (userAnswers, secondaryLegalContact, ukBased, ukAddress, internationalAddress) =>

        val answers =
          userAnswers
            .set(WhoIsSecondaryLegalContactPage, secondaryLegalContact).success.value
            .set(IsApplicantSecondaryLegalContactUkBasedPage, ukBased).success.value
            .set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress).success.value
            .set(ApplicantSecondaryLegalContactInternationalAddressPage, internationalAddress).success.value

        val result = answers.set(AddAnotherLegalContactPage, false).success.value

        result.get(WhoIsSecondaryLegalContactPage) must not be defined
        result.get(IsApplicantSecondaryLegalContactUkBasedPage) must not be defined
        result.get(ApplicantSecondaryLegalContactUkAddressPage) must not be defined
        result.get(ApplicantSecondaryLegalContactInternationalAddressPage) must not be defined
    }
  }

  "not remove the secondary legal contact data when the answer is yes" in {

    forAll(arbitrary[UserAnswers], arbitrary[WhoIsSecondaryLegalContact], arbitrary[Boolean],
      arbitrary[UkAddress], arbitrary[InternationalAddress]) {
      (userAnswers, secondaryLegalContact, ukBased, ukAddress, internationalAddress) =>

        val answers =
          userAnswers
            .set(WhoIsSecondaryLegalContactPage, secondaryLegalContact).success.value
            .set(IsApplicantSecondaryLegalContactUkBasedPage, ukBased).success.value
            .set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress).success.value
            .set(ApplicantSecondaryLegalContactInternationalAddressPage, internationalAddress).success.value

        val result = answers.set(AddAnotherLegalContactPage, true).success.value

        result.get(WhoIsSecondaryLegalContactPage) mustBe defined
        result.get(IsApplicantSecondaryLegalContactUkBasedPage) mustBe defined
        result.get(ApplicantSecondaryLegalContactUkAddressPage) mustBe defined
        result.get(ApplicantSecondaryLegalContactInternationalAddressPage) mustBe defined
    }
  }
}
