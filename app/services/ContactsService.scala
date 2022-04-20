/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package services

import models.afa.{Contact, RepresentativeContact}
import models.{Address, ApplicantLegalContact, ContactOptions, InternationalAddress, RepresentativeDetails, TechnicalContact, UkAddress, UserAnswers, WhoIsSecondaryLegalContact}
import pages._
import play.api.libs.json.Reads
import queries.Gettable

class ContactsService {

  private def get[A](query: Gettable[A])(implicit ua: UserAnswers, rds: Reads[A]): Option[A] = ua.get(query)

  def contactsToRadioOptions(removeContact: Option[ContactOptions])(implicit answers: UserAnswers): Seq[(ContactOptions, String)] = {
      getRequiredContacts(answers).filterNot(_.isEmpty).zipWithIndex.map {
        case(contact, index) =>
            (ContactOptions.values(index), contact.get.name)
      }.filterNot(_._1 == removeContact.getOrElse(None))
  }

  def getContactIsUKBased(contactOption: ContactOptions)(implicit answers: UserAnswers): Option[Boolean] = {
    contactOption match {
      case ContactOptions.RepresentativeContact => answers.get(IsRepresentativeContactUkBasedPage)
      case ContactOptions.LegalContact => answers.get(IsApplicantLegalContactUkBasedPage)
      case ContactOptions.SecondaryLegalContact => answers.get(IsApplicantSecondaryLegalContactUkBasedPage)
      case _ => throw new IllegalArgumentException("ContactsService.getContactIsUKBased: Illegal Contact Option")
    }
  }

  def getContactUKAddress(contactOption: ContactOptions)(implicit answers: UserAnswers): Option[UkAddress] = {
    contactOption match {
      case ContactOptions.RepresentativeContact => answers.get(RepresentativeContactUkAddressPage)
      case ContactOptions.LegalContact => answers.get(ApplicantLegalContactUkAddressPage)
      case ContactOptions.SecondaryLegalContact => answers.get(ApplicantSecondaryLegalContactUkAddressPage)
      case _ => throw new IllegalArgumentException("ContactsService.getContactUKAddress: Illegal Contact Option")
    }
  }

  def getContactInternationalAddress(contactOption: ContactOptions)(implicit answers: UserAnswers): Option[InternationalAddress] = {
    contactOption match {
      case ContactOptions.RepresentativeContact => answers.get(RepresentativeContactInternationalAddressPage)
      case ContactOptions.LegalContact => answers.get(ApplicantLegalContactInternationalAddressPage)
      case ContactOptions.SecondaryLegalContact => answers.get(ApplicantSecondaryLegalContactInternationalAddressPage)
      case _ => throw new IllegalArgumentException("ContactsService.getContactInternationalAddress: Illegal Contact Option")
    }
  }

  def getContact(contactOption: ContactOptions)(implicit answers: UserAnswers): TechnicalContact = {
    contactOption match {
      case ContactOptions.RepresentativeContact => convertContactToTechnical(getRepresentativeAsLegal)
      case ContactOptions.LegalContact => convertContactToTechnical(getLegalContactDetails)
      case ContactOptions.SecondaryLegalContact => convertContactToTechnical(getSecondaryLegalContactDetails)
      case _ => throw new IllegalArgumentException("ContactsService.getContact: Illegal Contact Option")
    }
  }

  def getRequiredContacts(implicit answers: UserAnswers): Seq[Option[Contact]] = {
    Seq(getRepresentativeAsLegal, getLegalContactDetails, getSecondaryLegalContactDetails)
  }

  private[services] def convertContactToTechnical(contact: Option[Contact]): TechnicalContact = {
    if(contact.isDefined) {
      TechnicalContact(contact.get.companyName, contact.get.name, contact.get.phone, contact.get.email)
    } else {
      throw new IllegalArgumentException("ContactsService.convertContactToTechnical: Contact Not Valid")
    }
  }

  def getRepresentativeContactDetails(implicit answers: UserAnswers): Option[RepresentativeContact] = {
    (get(RepresentativeDetailsPage), getRepresentativeAddress ,get(EvidenceOfPowerToActPage)) match {
      case (Some(RepresentativeDetails(name, companyName, phone, email, role)), Some(address), evidence) =>
        Some(RepresentativeContact(name, companyName, phone, email, role, address, evidence))
      case _ => None
    }
  }

  def getLegalContactDetails(implicit answers: UserAnswers): Option[Contact] = {
    (get(ApplicantLegalContactPage), getLegalContactAddress) match {
      case (Some(ApplicantLegalContact(companyName, name, phone, otherPhone, email)), Some(address)) =>
        Some(Contact(companyName, name, phone, otherPhone, email, address))
      case _ => None
    }
  }

  def getSecondaryLegalContactDetails(implicit answers: UserAnswers): Option[Contact] = {
    (get(WhoIsSecondaryLegalContactPage), getSecondaryLegalContactAddress) match {
      case (Some(WhoIsSecondaryLegalContact(companyName, name, phone, email)), Some(address)) =>
        Some(Contact(companyName, name, phone, None, email, address))
      case _ => None
    }
  }

  def getTechnicalContactDetails(implicit answers: UserAnswers): Option[Contact] = {
    (get(WhoIsTechnicalContactPage), getTechnicalContactAddress) match {
      case (Some(TechnicalContact(company, name, phone, email)), Some(address)) =>
        Some(Contact(company, name, phone, None, email, address))
      case _ =>
        None
    }
  }

  def getSecondaryTechnicalContactDetails(implicit answers: UserAnswers): Option[Contact] = {
    (get(WhoIsSecondaryTechnicalContactPage), getSecondaryTechnicalContactAddress) match {
      case (Some(TechnicalContact(company, name, phone, email)), Some(address)) =>
        Some(Contact(company, name, phone, None, email, address))
      case _ =>
        None
    }
  }

  private def getRepresentativeAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsRepresentativeContactUkBasedPage) flatMap {
      case true => get(RepresentativeContactUkAddressPage)
      case false => get(RepresentativeContactInternationalAddressPage)
    }
  }

  private def getRepresentativeAsLegal(implicit answers: UserAnswers): Option[Contact] = {
    (get(RepresentativeDetailsPage), getRepresentativeAddress) match {
      case (Some(RepresentativeDetails(name, companyName, phone, email, _)), Some(address)) =>
        Some(Contact(companyName, name, phone, None, email, address))
      case _ => None
    }
  }

  private def getLegalContactAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsApplicantLegalContactUkBasedPage) flatMap {
      case true => get(ApplicantLegalContactUkAddressPage)
      case false => get(ApplicantLegalContactInternationalAddressPage)
    }
  }

  private def getSecondaryLegalContactAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsApplicantSecondaryLegalContactUkBasedPage) flatMap {
      case true => get(ApplicantSecondaryLegalContactUkAddressPage)
      case false => get(ApplicantSecondaryLegalContactInternationalAddressPage)
    }
  }

  private def getTechnicalContactAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsTechnicalContactUkBasedPage) flatMap {
      case true => get(TechnicalContactUkAddressPage)
      case false => get(TechnicalContactInternationalAddressPage)
    }
  }

  private def getSecondaryTechnicalContactAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsSecondaryTechnicalContactUkBasedPage) flatMap {
      case true => get(SecondaryTechnicalContactUkAddressPage)
      case false => get(SecondaryTechnicalContactInternationalAddressPage)
    }
  }
}
