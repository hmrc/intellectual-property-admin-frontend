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

package utils

import controllers.routes
import models.{AfaId, CheckMode, InternationalAddress, Mode, UkAddress, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import queries.{IprDetailsQuery, NiceClassIdsQuery}
import utils.CheckYourAnswersHelper._
import viewmodels._

import java.time.format.DateTimeFormatter

//scalastyle:off number.of.methods
class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def additionalInfoProvided: Option[AnswerRowWithUrl] = userAnswers.get(AdditionalInfoProvidedPage) map {
    x =>
      new AnswerRowWithUrl(
        "additionalInfoProvided.checkYourAnswersLabel",
        yesOrNo(x),
        routes.AdditionalInfoProvidedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def restrictedHandling: Option[AnswerRowWithUrl] = userAnswers.get(RestrictedHandlingPage) map {
    x =>
      new AnswerRowWithUrl(
        "restrictedHandling.checkYourAnswersLabel",
        yesOrNo(x),
        routes.RestrictedHandlingController.onPageLoad(CheckMode, afaId).url)
  }

  def ipRightsSupplementaryProtectionCertificateType(mode: Mode, index: Int): Option[AnswerRowWithUrl] =
    userAnswers.get(IpRightsSupplementaryProtectionCertificateTypePage(index)) map {
      x =>
        new AnswerRowWithUrl(
          "ipRightsSupplementaryProtectionCertificateType.checkYourAnswersLabel",
          HtmlFormat.escape(messages(s"ipRightsSupplementaryProtectionCertificateType.$x")),
          routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(mode, index, afaId).url)
    }

  def ipRightsBrand(mode: Mode, index: Int): Option[AnswerRowWithUrl] = userAnswers.get(IpRightsDescriptionWithBrandPage(index)) map {
    x =>
      new AnswerRowWithUrl(
        "ipRightsDescriptionWithBrand.brand.checkYourAnswersLabel",
        HtmlFormat.escape(x.brand),
        routes.IpRightsDescriptionWithBrandController.onPageLoad(mode, index, afaId).url)
  }

  def ipRightsDescriptionWithBrand(mode: Mode, index: Int): Option[AnswerRowWithUrl] = userAnswers.get(IpRightsDescriptionWithBrandPage(index)) map {
    x =>
      new AnswerRowWithUrl(
        "ipRightsDescriptionWithBrand.description.checkYourAnswersLabel",
        HtmlFormat.escape(x.description),
        routes.IpRightsDescriptionWithBrandController.onPageLoad(mode, index, afaId).url
      )
  }

  def ipRightsDescription(mode: Mode, index: Int): Option[AnswerRowWithUrl] =
    userAnswers.get(IpRightsDescriptionPage(index)) map {
      x =>
        new AnswerRowWithUrl(
          "ipRightsDescription.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          routes.IpRightsDescriptionController.onPageLoad(mode, index, afaId).url
        )
    }

  def representativeEvidenceOfPower: Option[AnswerRow] = userAnswers.get(EvidenceOfPowerToActPage).map {
    x =>
      new AnswerRowWithUrl(
        "representativeEvidenceOfPowerToAct.checkYourAnswersLabel",
        yesOrNo(x),
        routes.EvidenceOfPowerToActController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsTechnicalContactEmail: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsTechnicalContact.email.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactEmail),
        routes.WhoIsTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsTechnicalContactPhone: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsTechnicalContact.contactTelephone.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactTelephone),
        routes.WhoIsTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsTechnicalContactName: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsTechnicalContact.name.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactName),
        routes.WhoIsTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsTechnicalContactCompany: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsTechnicalContactPage) map {
    x =>
          new AnswerRowWithUrl(
            "whoIsTechnicalContact.company.checkYourAnswersLabel",
            HtmlFormat.escape(x.companyName),
            routes.WhoIsTechnicalContactController.onPageLoad(CheckMode, afaId).url
          )
      }

  def whoIsSecondaryTechnicalContactName: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryTechnicalContact.name.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactName),
        routes.WhoIsSecondaryTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsSecondaryTechnicalContactCompany: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryTechnicalContact.companyName.checkYourAnswersLabel",
        HtmlFormat.escape(x.companyName),
        routes.WhoIsSecondaryTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsSecondaryTechnicalContactPhone: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryTechnicalContact.telephone.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactTelephone),
        routes.WhoIsSecondaryTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def whoIsSecondaryTechnicalContactEmail: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryTechnicalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryTechnicalContact.email.checkYourAnswersLabel",
        HtmlFormat.escape(x.contactEmail),
        routes.WhoIsSecondaryTechnicalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def isSecondaryTechnicalContactUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsSecondaryTechnicalContactUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isSecondaryTechnicalContactUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def secondaryTechnicalContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(SecondaryTechnicalContactUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "secondaryTechnicalContactUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.SecondaryTechnicalContactUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def secondaryTechnicalContactInternationalAddress: Option[AnswerRowWithUrl] = userAnswers.get(SecondaryTechnicalContactInternationalAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "secondaryTechnicalContactInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x),
        routes.SecondaryTechnicalContactInternationalAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactName: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeDetailsPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContact.name.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.contactName}"),
        routes.RepresentativeContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactCompanyName: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeDetailsPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContact.companyName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.companyName}"),
        routes.RepresentativeContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactRole: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeDetailsPage) flatMap {
    contact =>
      contact.roleOrPosition map {
        role =>
        new AnswerRowWithUrl(
          "representativeContact.role.checkYourAnswersLabel",
          HtmlFormat.escape(s"$role"),
          routes.RepresentativeContactController.onPageLoad(CheckMode, afaId).url
        )
      }
  }

  def representativeContactTelephone: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeDetailsPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContact.telephone.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.phone}"),
        routes.RepresentativeContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactEmail: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeDetailsPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContact.email.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.email}"),
        routes.RepresentativeContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def isRepresentativeUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsRepresentativeContactUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isRepresentativeContactUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsRepresentativeContactUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeContactUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContactUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.RepresentativeContactUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def representativeContactInternationalAddress: Option[AnswerRowWithUrl] = userAnswers.get(RepresentativeContactInternationalAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "representativeContactInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x),
        routes.RepresentativeContactInternationalAddressController.onPageLoad(CheckMode, afaId).url)
  }

  def applicantLegalContactName: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContact.name.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.name}"),
        routes.ApplicantLegalContactController.onPageLoad(CheckMode, afaId).url)
  }

  def applicantLegalCompanyName: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContact.companyName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.companyName}"),
        routes.ApplicantLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantLegalContactTelephone: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContact.telephone.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.telephone}"),
        routes.ApplicantLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantLegalContactOtherTelephone: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactPage) flatMap {
    contact =>
      contact.otherTelephone map {
        phone =>
          new AnswerRowWithUrl(
            "applicantLegalContact.otherTelephone.checkYourAnswersLabel",
            HtmlFormat.escape(phone),
            routes.ApplicantLegalContactController.onPageLoad(CheckMode, afaId).url
          )
      }
  }

  def applicantLegalContactEmail: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContact.email.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.email}"),
        routes.ApplicantLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def isApplicantLegalContactUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsApplicantLegalContactUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isApplicantLegalContactUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsApplicantLegalContactUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantSecondaryLegalContactName: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryLegalContact.name.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.contactName}"),
        routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode, afaId).url)
  }

  def applicantSecondaryLegalContactCompanyName: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryLegalContact.companyName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.companyName}"),
        routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantSecondaryLegalContactTelephone: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryLegalContact.telephone.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.contactTelephone}"),
        routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantSecondaryLegalContactEmail: Option[AnswerRowWithUrl] = userAnswers.get(WhoIsSecondaryLegalContactPage) map {
    x =>
      new AnswerRowWithUrl(
        "whoIsSecondaryLegalContact.email.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.contactEmail}"),
        routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode, afaId).url
      )
  }

  def isApplicantSecondaryLegalContactUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsApplicantSecondaryLegalContactUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isApplicantSecondaryLegalContactUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantSecondaryLegalContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantSecondaryLegalContactUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantSecondaryLegalContactUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.ApplicantSecondaryLegalContactUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantSecondaryLegalContactInternationalAddress: Option[AnswerRowWithUrl] =
    userAnswers.get(ApplicantSecondaryLegalContactInternationalAddressPage) map {
      x =>
        new AnswerRowWithUrl(
          "applicantSecondaryLegalContactInternationalAddress.checkYourAnswersLabel",
          internationalAddress(x),
          routes.ApplicantSecondaryLegalContactInternationalAddressController.onPageLoad(CheckMode, afaId).url
        )
    }

  def companyApplyingName: Option[AnswerRowWithUrl] = userAnswers.get(CompanyApplyingPage) map {
    x =>
      new AnswerRowWithUrl(
        "companyApplying.name.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.name}"),
        routes.CompanyApplyingController.onPageLoad(CheckMode, afaId).url
      )
  }

  def companyAcronym: Option[AnswerRowWithUrl] = userAnswers.get(CompanyApplyingPage) flatMap {
    x =>
      x.acronym map { y =>
        new AnswerRowWithUrl(
          "companyApplying.acronym.checkYourAnswersLabel",
          HtmlFormat.escape(s"$y"),
          routes.CompanyApplyingController.onPageLoad(CheckMode, afaId).url
        )
      }
  }

  def isCompanyApplyingUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsCompanyApplyingUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isCompanyApplyingUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsCompanyApplyingUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def companyApplyingInternationalAddress: Option[AnswerRowWithUrl] = userAnswers.get(CompanyApplyingInternationalAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "companyApplyingInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x),
        routes.CompanyApplyingInternationalAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def companyApplyingContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(CompanyApplyingUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "companyApplyingUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.CompanyApplyingUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def companyApplyingIsRightsHolder: Option[AnswerRowWithUrl] = userAnswers.get(CompanyApplyingIsRightsHolderPage) map {
    x =>
      new AnswerRowWithUrl(
        "companyApplyingIsRightsHolder.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"companyApplyingIsRightsHolder.${x.toString}")),
        routes.CompanyApplyingIsRightsHolderController.onPageLoad(CheckMode, afaId).url
      )
  }

  def ipRightsRegistrationEnd(mode: Mode, index: Int): Option[AnswerRowWithUrl] = userAnswers.get(IpRightsRegistrationEndPage(index)) map {
    x =>
      new AnswerRowWithUrl(
        "ipRightsRegistrationEnd.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.IpRightsRegistrationEndController.onPageLoad(mode, index, afaId).url
      )
  }

  def ipRightsRegistrationNumber(mode: Mode, index: Int): Option[AnswerRowWithUrl] = userAnswers.get(IpRightsRegistrationNumberPage(index)) map {
    x =>
      new AnswerRowWithUrl(
        "ipRightsRegistrationNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.IpRightsRegistrationNumberController.onPageLoad(mode, index, afaId).url
      )
  }

  def permissionToDestroySmallConsignments: Option[AnswerRowWithUrl] = userAnswers.get(PermissionToDestroySmallConsignmentsPage) map {
    x =>
      new AnswerRowWithUrl(
        "permissionToDestroySmallConsignments.checkYourAnswersLabel",
        yesOrNo(x),
        routes.PermissionToDestroySmallConsignmentsController.onPageLoad(CheckMode, afaId).url
      )
  }

  def wantsOneYearRightsProtection: Option[AnswerRowWithUrl] = userAnswers.get(WantsOneYearRightsProtectionPage) map {
    x =>
      new AnswerRowWithUrl(
        "wantsOneYearRightsProtection.checkYourAnswersLabel",
        yesOrNo(x),
        routes.WantsOneYearRightsProtectionController.onPageLoad(CheckMode, afaId).url
      )
  }

  def ipRightsType(mode: Mode, index: Int): Option[AnswerRowWithUrl] = userAnswers.get(IpRightsTypePage(index)) map {
    x =>
      new AnswerRowWithUrl(
        "ipRightsType.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"ipRightsType.$x")),
        routes.IpRightsTypeController.onPageLoad(mode, index, afaId).url
      )
  }

  def isExOfficio: Option[AnswerRowWithUrl] = userAnswers.get(IsExOfficioPage) map {
    x =>
      new AnswerRowWithUrl(
        "isExOfficio.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsExOfficioController.onPageLoad(CheckMode, afaId).url
      )
  }

  def technicalContactInternationalAddress: Option[AnswerRowWithUrl] = userAnswers.get(TechnicalContactInternationalAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "technicalContactInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x),
        routes.TechnicalContactInternationalAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantLegalContactInternationalAddress: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactInternationalAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContactInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x),
        routes.ApplicantLegalContactInternationalAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def technicalContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(TechnicalContactUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "technicalContactUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.TechnicalContactUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicantLegalContactUkAddress: Option[AnswerRowWithUrl] = userAnswers.get(ApplicantLegalContactUkAddressPage) map {
    x =>
      new AnswerRowWithUrl(
        "applicantLegalContactUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.ApplicantLegalContactUkAddressController.onPageLoad(CheckMode, afaId).url
      )
  }

  def isTechnicalContactUkBased: Option[AnswerRowWithUrl] = userAnswers.get(IsTechnicalContactUkBasedPage) map {
    x =>
      new AnswerRowWithUrl(
        "isTechnicalContactUkBased.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsTechnicalContactUkBasedController.onPageLoad(CheckMode, afaId).url
      )
  }

  def applicationReceiptDate: Option[AnswerRowWithUrl] = userAnswers.get(ApplicationReceiptDatePage) map {
    x =>
      new AnswerRowWithUrl(
        "applicationReceiptDate.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.ApplicationReceiptDateController.onPageLoad(CheckMode, afaId).url
      )
  }

  def niceClasses(mode: Mode, iprIndex: Int): Option[Seq[AnswerRowWithUrl]] = userAnswers.get(NiceClassIdsQuery(iprIndex)).map {
    niceClasses =>
      niceClasses.zipWithIndex.map {
        case (niceClass, niceClassIndex) =>
          NiceClassAnswerRowWithUrl(
            niceClass.toString,
            HtmlFormat.escape(""),
            routes.IpRightsNiceClassController.onPageLoad(mode, iprIndex, niceClassIndex, afaId).url
          )
      }
  }

  lazy val afaId: AfaId = userAnswers.id

  private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }

  private def ukAddress(address: UkAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        address.line2.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.town)),
        address.county.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.postCode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  private def internationalAddress(address: InternationalAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        address.line2.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.town)),
        Some(HtmlFormat.escape(address.country)),
        address.postCode.map(HtmlFormat.escape)
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def applicationSection: Option[AnswerSection] =
    (applicationReceiptDate).map(
      _ =>
        AnswerSection(
          Some("checkYourAnswers.application"),
          Seq(
            applicationReceiptDate,
            companyApplyingName,
            companyAcronym,
            isCompanyApplyingUkBased,
            companyApplyingContactUkAddress,
            companyApplyingInternationalAddress,
            companyApplyingIsRightsHolder
          ).flatten
        )
    )

  def representativeSection: Option[AnswerSection] =
    representativeContactName.map(
      _ =>
        AnswerSection(
          Some("checkYourAnswers.representativeContact"),
          Seq(
            representativeContactName,
            representativeContactCompanyName,
            representativeContactTelephone,
            representativeContactEmail,
            representativeContactRole,
            isRepresentativeUkBased,
            representativeContactUkAddress,
            representativeContactInternationalAddress,
            representativeEvidenceOfPower
          ).flatten
        )
    )

  def legalContactSection: Option[AnswerSection] =
    applicantLegalContactName.map(
      _ =>
        AnswerSection(
          Some("checkYourAnswers.legalContact"),
          Seq(
            applicantLegalContactName,
            applicantLegalCompanyName,
            applicantLegalContactTelephone,
            applicantLegalContactOtherTelephone,
            applicantLegalContactEmail,
            isApplicantLegalContactUkBased,
            applicantLegalContactUkAddress,
            applicantLegalContactInternationalAddress
          ).flatten
        )
    )

  def secondaryLegalContactSection: Option[AnswerSection] =
    applicantLegalContactName.map(
      _ =>

        applicantSecondaryLegalContactName.fold(
          AnswerSection(None, Seq(),
            Some(SectionLink(controllers.routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode,
              afaId).url, "checkYourAnswers.addAnotherLegalContact")))
        )(
          _ =>
            AnswerSection(
              Some("checkYourAnswers.secondaryLegalContact"),
              Seq(
                applicantSecondaryLegalContactName,
                applicantSecondaryLegalContactCompanyName,
                applicantSecondaryLegalContactTelephone,
                applicantSecondaryLegalContactEmail,
                isApplicantSecondaryLegalContactUkBased,
                applicantSecondaryLegalContactUkAddress,
                applicantSecondaryLegalContactInternationalAddress
              ).flatten
              , Some(SectionLink(controllers.routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, "legal").url,
                "checkYourAnswers.removeSecondaryLegalContact")))
        )
    )

  def technicalContactSection: Option[AnswerSection] =
    whoIsTechnicalContactName.map(
      _ =>
        AnswerSection(
          Some("checkYourAnswers.technicalContact"),
          Seq(
            whoIsTechnicalContactName,
            whoIsTechnicalContactCompany,
            whoIsTechnicalContactPhone,
            whoIsTechnicalContactEmail,
            isTechnicalContactUkBased,
            technicalContactUkAddress,
            technicalContactInternationalAddress
          ).flatten
        )
    )

  def secondaryTechnicalContactSection: Option[AnswerSection] =

    whoIsTechnicalContactName.map(
      _ =>

        whoIsSecondaryTechnicalContactName.fold(
          AnswerSection(None, Seq(),
            Some(SectionLink(controllers.routes.SelectOtherTechnicalContactController.onPageLoad(CheckMode,
              afaId).url, "checkYourAnswers.addAnotherTechContact")))
        )(
          _ => AnswerSection(
            Some("checkYourAnswers.secondaryTechnicalContact"),
            Seq(
              whoIsSecondaryTechnicalContactName,
              whoIsSecondaryTechnicalContactCompany,
              whoIsSecondaryTechnicalContactPhone,
              whoIsSecondaryTechnicalContactEmail,
              isSecondaryTechnicalContactUkBased,
              secondaryTechnicalContactUkAddress,
              secondaryTechnicalContactInternationalAddress
            ).flatten
            , Some(SectionLink(controllers.routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, "technical").url,
              "checkYourAnswers.removeSecondaryTechnicalContact")))
        )
    )

  val iprDetails: Option[List[AnswerRow]] = userAnswers.get(IprDetailsQuery) match {
    case a@Some(iprs) if iprs.nonEmpty =>
      a.map(_.zipWithIndex.map {
        case (ipr, index) =>
          IpRightAnswerRowWithUrl(
            ipr.rightsType.map(t => s"ipRightsType.${t.toString}").getOrElse(""),
            HtmlFormat.escape(ipr.registrationNumber.getOrElse(ipr.description.getOrElse(""))),
            controllers.routes.CheckIprDetailsController.onPageLoad(CheckMode, index, afaId).url,
            index + 1
          )
      })

    case _ =>
      Some(List(DisplayAnswerRow("ipRightsType.noIPRights", HtmlFormat.escape(""))))
  }

  private val numberOfIprs: Int = userAnswers.get(IprDetailsQuery).fold(0)(_.size)

  def ipRightsSection(canCreateAfa: Boolean): Option[AnswerSection] =
    iprDetails.map {
      answerRows =>

        val message = if (numberOfIprs == 1) {
          Some(messages("checkYourAnswers.ipRights", numberOfIprs))
        } else {
          Some(messages("checkYourAnswers.ipRights.plural", numberOfIprs))
        }

        if (canCreateAfa) {
          AnswerSection(message, answerRows,
            Some(SectionLink(routes.AddIpRightController.onPageLoad(CheckMode, afaId).url, "checkYourAnswers.addIpRight.link")))
        } else {
          AnswerSection(message, answerRows)
        }
    }

  def additionalInformationSection: Option[AnswerSection] = {
    additionalInfoProvided.map(
      _ =>
        AnswerSection(
          Some("checkYourAnswers.additionalInformation"),
          Seq(
            additionalInfoProvided,
            restrictedHandling,
            permissionToDestroySmallConsignments,
            isExOfficio,
            wantsOneYearRightsProtection
          ).flatten
        )
    )
  }
  //scalastyle:on number.of.methods
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
