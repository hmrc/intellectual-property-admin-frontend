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

package constants

import models.{ApplicantLegalContact, ContactOptions, InternationalAddress, RepresentativeDetails, TechnicalContact, UkAddress, WhoIsSecondaryLegalContact}
import models.afa.{Contact, RepresentativeContact}

object ContactsConstants {

  val companyApplyingName = "companyName"
  val companyApplyingAcronym = "companyAcronym"
  val ukAddress: UkAddress = UkAddress("street", None, "town", None, "postcode")
  val internationalAddress: InternationalAddress = InternationalAddress("street", None, "town", "country", None)
  val representativeDetails: RepresentativeDetails = RepresentativeDetails("repContact", "companyName", "phone", "email", Some("ceo"))
  val evidenceOfPowerToActTrue = true
  val evidenceOfPowerToActFalse = false
  val applicantLegalDetails: ApplicantLegalContact = ApplicantLegalContact("companyName", "legalContact", "phone", Some("otherPhone"), "email")
  val applicantSecondaryLegalDetails: WhoIsSecondaryLegalContact = WhoIsSecondaryLegalContact("otherLegalCompany", "otherLegalContact", "phone", "email")
  val technicalContactDetails: TechnicalContact = TechnicalContact("techContactCompany", "techContactName", "phone", "email")
  val secondaryTechnicalContactDetails: TechnicalContact = TechnicalContact("secondaryTechContactCompany", "secondaryTechContactName", "phone", "email")

  val representativeContact: RepresentativeContact = RepresentativeContact(
    representativeDetails.contactName,
    representativeDetails.companyName,
    representativeDetails.phone,
    representativeDetails.email,
    representativeDetails.roleOrPosition,
    ukAddress,
    Some(evidenceOfPowerToActTrue)
  )

  val representativeContactAsLegal: Contact = Contact(
    companyApplyingName,
    representativeContact.contactName,
    representativeContact.phone,
    None,
    representativeContact.email,
    ukAddress
  )

  val applicantLegalContact: Contact = Contact(
    applicantLegalDetails.companyName,
    applicantLegalDetails.name,
    applicantLegalDetails.telephone,
    applicantLegalDetails.otherTelephone,
    applicantLegalDetails.email,
    ukAddress
  )

  val applicantSecondaryLegalContact: Contact = Contact(
    applicantSecondaryLegalDetails.companyName,
    applicantSecondaryLegalDetails.contactName,
    applicantSecondaryLegalDetails.contactTelephone,
    None,
    applicantSecondaryLegalDetails.contactEmail,
    ukAddress
  )

  val technicalContactAsContact: Contact = Contact(
    technicalContactDetails.companyName,
    technicalContactDetails.contactName,
    technicalContactDetails.contactTelephone,
    None,
    technicalContactDetails.contactEmail,
    ukAddress
  )

  val secondaryContactAsContact: Contact = Contact(
    secondaryTechnicalContactDetails.companyName,
    secondaryTechnicalContactDetails.contactName,
    secondaryTechnicalContactDetails.contactTelephone,
    None,
    secondaryTechnicalContactDetails.contactEmail,
    ukAddress
  )

  val radioOptionsRep: (ContactOptions, String) = (ContactOptions.RepresentativeContact, "repContact")
  val radioOptionsLegal: (ContactOptions, String) = (ContactOptions.LegalContact, "legalContact")

}
