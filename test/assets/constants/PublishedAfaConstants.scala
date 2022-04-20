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
