/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package constants

import java.time.LocalDate

import models.{AfaId, CompanyApplyingIsRightsHolder}
import models.afa.{Company, ExOfficio, PublishedAfa, SemiconductorTopography}
import constants.ContactsConstants.{applicantLegalContact, technicalContactAsContact, ukAddress, representativeContact}

object PublishedAfaConstants {

  val localDatePast: LocalDate = LocalDate.now.minusYears(1)
  val localDateFuture: LocalDate = LocalDate.now.plusYears(1)

  val publishedAfa: PublishedAfa =
    PublishedAfa(
      id = AfaId.fromString("UK20200001").get,
      receiptDate = Some(localDatePast),
      additionalInfoProvided = true,
      shareWithEuropeanCommission = Some(true),
      permissionToDestroySmallConsignments = true,
      exOfficio = Some(ExOfficio(wantsOneYearProtection = true)),
      applicant = Company(
        "name",
        Some("acronym"),
        applicantAddressUkBased = true,
        ukAddress,
        CompanyApplyingIsRightsHolder.RightsHolder
      ),
      legalContact = applicantLegalContact,
      secondaryLegalContact = Some(applicantLegalContact),
      technicalContact = technicalContactAsContact,
      secondaryTechnicalContact = Some(technicalContactAsContact),
      ipRights = Seq(SemiconductorTopography("description")),
      endDate = localDateFuture,
      expirationDate = localDateFuture,
      representativeContact = representativeContact,
      isRestrictedHandling = Some(true)


    )

}
