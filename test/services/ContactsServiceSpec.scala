/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import constants.ContactsConstants._
import generators.AfaGenerators
import models.afa.Contact
import models.{ApplicantLegalContact, CompanyApplying, CompanyApplyingIsRightsHolder, ContactOptions, InternationalAddress, TechnicalContact, UkAddress, UserAnswers, WhoIsSecondaryLegalContact}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages._

class ContactsServiceSpec extends AnyFreeSpec with Matchers with AfaGenerators with TryValues with OptionValues {

  implicit val userAnswers: UserAnswers = UserAnswers(arbitraryGbAfaIdThreeDigit().sample.value)
    .set(CompanyApplyingPage, CompanyApplying(companyApplyingName, None))
    .success
    .value
    .set(IsCompanyApplyingUkBasedPage, true)
    .success
    .value
    .set(CompanyApplyingUkAddressPage, ukAddress)
    .success
    .value
    .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.Authorised)
    .success
    .value
    .set(RepresentativeDetailsPage, representativeDetails)
    .success
    .value
    .set(IsRepresentativeContactUkBasedPage, true)
    .success
    .value
    .set(RepresentativeContactUkAddressPage, ukAddress)
    .success
    .value
    .set(EvidenceOfPowerToActPage, evidenceOfPowerToActTrue)
    .success
    .value
    .set(IsRepresentativeContactLegalContactPage, false)
    .success
    .value
    .set(ApplicantLegalContactPage, applicantLegalDetails)
    .success
    .value
    .set(IsApplicantLegalContactUkBasedPage, true)
    .success
    .value
    .set(ApplicantLegalContactUkAddressPage, ukAddress)
    .success
    .value
    .set(WhoIsTechnicalContactPage, technicalContactDetails)
    .success
    .value
    .set(IsTechnicalContactUkBasedPage, true)
    .success
    .value
    .set(TechnicalContactUkAddressPage, ukAddress)
    .success
    .value
    .set(WhoIsSecondaryLegalContactPage, applicantSecondaryLegalDetails)
    .success
    .value
    .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
    .success
    .value
    .set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress)
    .success
    .value
    .set(IsSecondaryTechnicalContactUkBasedPage, true)
    .success
    .value
    .set(WhoIsSecondaryTechnicalContactPage, secondaryTechnicalContactDetails)
    .success
    .value
    .set(SecondaryTechnicalContactUkAddressPage, ukAddress)
    .success
    .value

  val service: ContactsService = new ContactsService

  "ContactsService" - {
    "getRepresentativeContact" - {
      "must return None when contact has missing values" in {
        implicit val implicitUserAnswers: UserAnswers =
          userAnswers.remove(RepresentativeContactUkAddressPage).success.value

        service.getRepresentativeContactDetails(implicitUserAnswers) mustBe None
      }
      "must return representative contact when all fields are filled in" in {
        service.getRepresentativeContactDetails mustBe Some(representativeContact)
      }
    }

    "getApplicantLegalContact" - {
      "must return None when contact has missing values" in {
        implicit val implicitUserAnswers: UserAnswers =
          userAnswers.remove(ApplicantLegalContactUkAddressPage).success.value

        service.getLegalContactDetails(implicitUserAnswers) mustBe None
      }
      "must return legal contact when all fields are filled in" in {
        service.getLegalContactDetails mustBe Some(applicantLegalContact)
      }
      "must return representative contact as legal when user answers yes to rep same as legal" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers
          .set(IsRepresentativeContactLegalContactPage, true)
          .success
          .value

        service.getLegalContactDetails(implicitUserAnswers) mustBe
          Some(
            Contact(
              representativeDetails.companyName,
              representativeDetails.contactName,
              representativeDetails.phone,
              None,
              representativeDetails.email,
              ukAddress
            )
          )
      }
    }

    "getTechnicalContact" - {
      "must return None when contact has missing values" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers.remove(TechnicalContactUkAddressPage).success.value

        service.getTechnicalContactDetails(implicitUserAnswers) mustBe None
      }
      "must return technical contact when whoIsTechnicalContact fields are filled in" in {
        service.getTechnicalContactDetails mustBe Some(technicalContactAsContact)
      }
    }

    "getSecondaryLegalContact" - {
      "must return secondaryLegal contact when secondaryLegalContact fields are filled in" in {
        service.getSecondaryLegalContactDetails mustBe Some(applicantSecondaryLegalContact)
      }
      "must return None when contact has missing values" in {
        implicit val implicitUserAnswers: UserAnswers =
          userAnswers.remove(ApplicantSecondaryLegalContactUkAddressPage).success.value

        service.getSecondaryLegalContactDetails(implicitUserAnswers) mustBe None
      }
    }

    "getSecondaryTechnicalContact" - {
      "must return secondaryTechnical contact when SecondaryTechnicalContact fields are filled in" in {
        service.getSecondaryTechnicalContactDetails mustBe Some(secondaryContactAsContact)
      }
      "must return None when contact has missing values" in {
        implicit val implicitUserAnswers: UserAnswers =
          userAnswers.remove(SecondaryTechnicalContactUkAddressPage).success.value

        service.getSecondaryTechnicalContactDetails(implicitUserAnswers) mustBe None
      }
    }

    "contactsToRadioOptions" - {

      "must return an empty sequence when no complete rep, legal or otherLegal contact exists" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers
          .remove(ApplicantLegalContactUkAddressPage)
          .success
          .value
          .remove(RepresentativeContactUkAddressPage)
          .success
          .value
          .remove(ApplicantSecondaryLegalContactUkAddressPage)
          .success
          .value

        service.contactsToRadioOptions(None)(implicitUserAnswers) mustBe Seq.empty
      }

      "when only rep contact is filled must return only a rep contact name with an index" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers
          .remove(ApplicantLegalContactUkAddressPage)
          .success
          .value
          .remove(ApplicantSecondaryLegalContactUkAddressPage)
          .success
          .value

        service.contactsToRadioOptions(None)(implicitUserAnswers) mustBe Seq(
          (ContactOptions.RepresentativeContact, representativeDetails.contactName)
        )
      }

      "when only legal contact is filled must return only a legal contact name with an index" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers
          .remove(RepresentativeContactUkAddressPage)
          .success
          .value
          .remove(ApplicantSecondaryLegalContactUkAddressPage)
          .success
          .value

        service.contactsToRadioOptions(None)(implicitUserAnswers) mustBe Seq(
          (ContactOptions.RepresentativeContact, applicantLegalDetails.name)
        )
      }

      "when only secondaryLegal contact is filled must return only a secondaryLegal contact name with an index" in {
        implicit val implicitUserAnswers: UserAnswers = userAnswers
          .remove(RepresentativeContactUkAddressPage)
          .success
          .value
          .remove(ApplicantLegalContactUkAddressPage)
          .success
          .value

        service.contactsToRadioOptions(None)(implicitUserAnswers) mustBe Seq(
          (ContactOptions.RepresentativeContact, applicantSecondaryLegalDetails.contactName)
        )
      }

      "must return rep and all legal contacts with index and name" in {
        service.contactsToRadioOptions(None) mustBe Seq(
          (ContactOptions.RepresentativeContact, representativeDetails.contactName),
          (ContactOptions.LegalContact, applicantLegalDetails.name),
          (ContactOptions.SecondaryLegalContact, applicantSecondaryLegalDetails.contactName)
        )
      }

      "must remove the correct contact when passed remove contact" - {
        "remove the rep contact" in {
          service.contactsToRadioOptions(Some(ContactOptions.RepresentativeContact)) mustBe Seq(
            (ContactOptions.LegalContact, applicantLegalDetails.name),
            (ContactOptions.SecondaryLegalContact, applicantSecondaryLegalDetails.contactName)
          )
        }
        "remove the legal contact" in {
          service.contactsToRadioOptions(Some(ContactOptions.LegalContact)) mustBe Seq(
            (ContactOptions.RepresentativeContact, representativeDetails.contactName),
            (ContactOptions.SecondaryLegalContact, applicantSecondaryLegalDetails.contactName)
          )
        }
        "remove the secondary legal contact" in {
          service.contactsToRadioOptions(Some(ContactOptions.SecondaryLegalContact)) mustBe Seq(
            (ContactOptions.RepresentativeContact, representativeDetails.contactName),
            (ContactOptions.LegalContact, applicantLegalDetails.name)
          )
        }
        "do nothing for someone-else" in {
          service.contactsToRadioOptions(Some(ContactOptions.SomeoneElse)) mustBe Seq(
            (ContactOptions.RepresentativeContact, representativeDetails.contactName),
            (ContactOptions.LegalContact, applicantLegalDetails.name),
            (ContactOptions.SecondaryLegalContact, applicantSecondaryLegalDetails.contactName)
          )
        }
      }
    }

    "getContactIsUkBased must return the correct values for each contact option" in {
      implicit val implicitUserAnswers: UserAnswers = userAnswers
        .set(IsApplicantSecondaryLegalContactUkBasedPage, false)
        .success
        .value

      service.getContactIsUKBased(ContactOptions.RepresentativeContact)(implicitUserAnswers) mustBe Some(true)
      service.getContactIsUKBased(ContactOptions.LegalContact)(implicitUserAnswers) mustBe Some(true)
      service.getContactIsUKBased(ContactOptions.SecondaryLegalContact)(implicitUserAnswers) mustBe Some(false)
      assertThrows[IllegalArgumentException](
        service.getContactIsUKBased(ContactOptions.SomeoneElse)(implicitUserAnswers)
      )
    }

    "getContactUKAddress must return the correct values for each contact option" in {
      val ukAddress2: UkAddress = UkAddress("street2", None, "town", None, "postcode")
      val ukAddress3: UkAddress = UkAddress("street3", None, "town", None, "postcode")

      implicit val implicitUserAnswers: UserAnswers = userAnswers
        .set(ApplicantLegalContactUkAddressPage, ukAddress2)
        .success
        .value
        .set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress3)
        .success
        .value

      service.getContactUKAddress(ContactOptions.RepresentativeContact)(implicitUserAnswers) mustBe Some(ukAddress)
      service.getContactUKAddress(ContactOptions.LegalContact)(implicitUserAnswers) mustBe Some(ukAddress2)
      service.getContactUKAddress(ContactOptions.SecondaryLegalContact)(implicitUserAnswers) mustBe Some(ukAddress3)
      assertThrows[IllegalArgumentException](
        service.getContactUKAddress(ContactOptions.SomeoneElse)(implicitUserAnswers)
      )
    }

    "getContactInternationalAddress must return the correct values for each contact option" in {
      val internationalAddress2: InternationalAddress = InternationalAddress("street2", None, "town", "country", None)
      val internationalAddress3: InternationalAddress = InternationalAddress("street3", None, "town", "country", None)

      implicit val implicitUserAnswers: UserAnswers = userAnswers
        .set(RepresentativeContactInternationalAddressPage, internationalAddress)
        .success
        .value
        .set(ApplicantLegalContactInternationalAddressPage, internationalAddress2)
        .success
        .value
        .set(ApplicantSecondaryLegalContactInternationalAddressPage, internationalAddress3)
        .success
        .value

      service.getContactInternationalAddress(ContactOptions.RepresentativeContact)(implicitUserAnswers) mustBe Some(
        internationalAddress
      )
      service.getContactInternationalAddress(ContactOptions.LegalContact)(implicitUserAnswers) mustBe Some(
        internationalAddress2
      )
      service.getContactInternationalAddress(ContactOptions.SecondaryLegalContact)(implicitUserAnswers) mustBe Some(
        internationalAddress3
      )
      assertThrows[IllegalArgumentException](
        service.getContactInternationalAddress(ContactOptions.SomeoneElse)(implicitUserAnswers)
      )
    }

    "getContact must return the correct values for each contact option" in {
      val applicantLegalContactDetails: ApplicantLegalContact               =
        ApplicantLegalContact("legalCompany", "legalName", "legalTelephone", None, "legalEmail")
      val applicantSecondaryLegalContactDetails: WhoIsSecondaryLegalContact =
        WhoIsSecondaryLegalContact("legalCompany", "legalName", "legalTelephone", "legalEmail")

      implicit val implicitUserAnswers: UserAnswers = userAnswers
        .set(ApplicantLegalContactPage, applicantLegalContactDetails)
        .success
        .value
        .set(WhoIsSecondaryLegalContactPage, applicantSecondaryLegalContactDetails)
        .success
        .value

      service.getContact(ContactOptions.RepresentativeContact)(implicitUserAnswers) mustBe
        TechnicalContact(
          representativeDetails.companyName,
          representativeDetails.contactName,
          representativeDetails.phone,
          representativeDetails.email
        )
      service.getContact(ContactOptions.LegalContact)(implicitUserAnswers) mustBe
        TechnicalContact(
          applicantLegalContactDetails.companyName,
          applicantLegalContactDetails.name,
          applicantLegalContactDetails.telephone,
          applicantLegalContactDetails.email
        )
      service.getContact(ContactOptions.SecondaryLegalContact)(implicitUserAnswers) mustBe
        TechnicalContact(
          applicantSecondaryLegalContactDetails.companyName,
          applicantSecondaryLegalContactDetails.contactName,
          applicantSecondaryLegalContactDetails.contactTelephone,
          applicantSecondaryLegalContactDetails.contactEmail
        )
      assertThrows[IllegalArgumentException](service.getContact(ContactOptions.SomeoneElse)(implicitUserAnswers))
    }

    "getRequiredContacts must return contacts when called" in {
      service.getRequiredContacts mustBe Seq(
        Some(representativeContactAsLegal),
        Some(applicantLegalContact),
        Some(applicantSecondaryLegalContact)
      )
    }

    "convertContactToTechnical" - {
      "return an exception when contact is not defined" in {
        assertThrows[IllegalArgumentException](service.convertContactToTechnical(None))
      }
      "return a technicalContact when defined" in {
        service.convertContactToTechnical(Some(representativeContactAsLegal)) mustBe
          TechnicalContact(
            representativeContact.companyName,
            representativeContact.contactName,
            representativeContact.phone,
            representativeContact.email
          )
      }
    }

  }
}
