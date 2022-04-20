/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{ApplicantLegalContact, InternationalAddress, RepresentativeDetails, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsRepresentativeContactLegalContactPageSpec extends PageBehaviours {

  val representativeContactName = "representative contact name"
  val representativeContact: RepresentativeDetails = RepresentativeDetails(representativeContactName, "companyName", "01234567890", "email", Some("roleOrPosition"))
  val ukRepAddress: UkAddress = UkAddress("Grange Central", None, "Telford", None, "TF34ER")
  val internationalRepAddress: InternationalAddress = InternationalAddress("Rue Florent", None, "Marseille", "France", None)

  "IsRepresentativeContactLegalContactPageSpec" must {

    "be settable and gettable with prerequisites" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsRepresentativeContactUkBasedPage, true).success.value
            .set(RepresentativeDetailsPage, representativeContact).success.value
            .set(RepresentativeContactUkAddressPage, ukRepAddress).success.value
            .set(IsRepresentativeContactLegalContactPage, true).success.value

          answers.get(IsRepresentativeContactLegalContactPage) mustBe Some(true)
      }
    }

    "be removable with prerequisites" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsRepresentativeContactUkBasedPage, true).success.value
            .set(RepresentativeDetailsPage, representativeContact).success.value
            .set(RepresentativeContactUkAddressPage, ukRepAddress).success.value
            .set(IsRepresentativeContactLegalContactPage, true).success.value

          answers.get(IsRepresentativeContactLegalContactPage) mustBe Some(true)

          val result = answers.remove(IsRepresentativeContactLegalContactPage).success.value

          result.get(IsRepresentativeContactLegalContactPage) must not be defined
      }
    }

    beRequired[Boolean](IsRepresentativeContactLegalContactPage)

    "replace all legal contact data with representative data when the answer is yes and address is UK based" in {

      forAll(arbitrary[UserAnswers], arbitrary[ApplicantLegalContact], arbitrary[Boolean], arbitrary[UkAddress], arbitrary[InternationalAddress]) {
        (userAnswers, applicantLegalContact, ukBased, ukAddress, internationalAddress) =>

          val answers =
            userAnswers
              .set(ApplicantLegalContactPage, applicantLegalContact).success.value
              .set(IsApplicantLegalContactUkBasedPage, ukBased).success.value
              .set(ApplicantLegalContactUkAddressPage, ukAddress).success.value
              .set(ApplicantLegalContactInternationalAddressPage, internationalAddress).success.value
              .set(RepresentativeDetailsPage, representativeContact).success.value
              .set(IsRepresentativeContactUkBasedPage, true).success.value
              .set(RepresentativeContactUkAddressPage, ukRepAddress).success.value

          val result = answers.set(IsRepresentativeContactLegalContactPage, true).success.value

          result.get(ApplicantLegalContactPage) mustBe Some(representativeContact.getAsLegal)
          result.get(IsApplicantLegalContactUkBasedPage) mustBe Some(true)
          result.get(ApplicantLegalContactUkAddressPage) mustBe Some(ukRepAddress)
          result.get(ApplicantLegalContactInternationalAddressPage) must not be defined
      }
    }

    "replace all legal contact data with representative data when the answer is yes and address is non UK based" in {

      forAll(arbitrary[UserAnswers], arbitrary[ApplicantLegalContact], arbitrary[Boolean], arbitrary[UkAddress], arbitrary[InternationalAddress]) {
        (userAnswers, applicantLegalContact, ukBased, ukAddress, internationalAddress) =>

          val answers =
            userAnswers
              .set(ApplicantLegalContactPage, applicantLegalContact).success.value
              .set(IsApplicantLegalContactUkBasedPage, ukBased).success.value
              .set(ApplicantLegalContactUkAddressPage, ukAddress).success.value
              .set(ApplicantLegalContactInternationalAddressPage, internationalAddress).success.value
              .set(RepresentativeDetailsPage, representativeContact).success.value
              .set(IsRepresentativeContactUkBasedPage, false).success.value
              .set(RepresentativeContactInternationalAddressPage, internationalRepAddress).success.value

          val result = answers.set(IsRepresentativeContactLegalContactPage, true).success.value

          result.get(ApplicantLegalContactPage) mustBe Some(representativeContact.getAsLegal)
          result.get(IsApplicantLegalContactUkBasedPage) mustBe Some(false)
          result.get(ApplicantLegalContactUkAddressPage) must not be defined
          result.get(ApplicantLegalContactInternationalAddressPage) mustBe Some(internationalRepAddress)
      }
    }
  }
}
