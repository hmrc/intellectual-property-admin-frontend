/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import java.time.LocalDate

import models.AfaId
import play.api.libs.json.{Json, OFormat}

final case class PublishedAfa(id: AfaId,
                              receiptDate: Option[LocalDate],
                              additionalInfoProvided: Boolean,
                              shareWithEuropeanCommission: Option[Boolean],
                              permissionToDestroySmallConsignments: Boolean,
                              exOfficio: Option[ExOfficio],
                              applicant: Company,
                              legalContact: Contact,
                              secondaryLegalContact: Option[Contact],
                              technicalContact: Contact,
                              secondaryTechnicalContact: Option[Contact],
                              ipRights: Seq[IpRight],
                              endDate: LocalDate,
                              expirationDate: LocalDate,
                              representativeContact: RepresentativeContact,
                              isRestrictedHandling: Option[Boolean]) {

  def isExOfficio: Boolean = exOfficio.isDefined
}

object PublishedAfa {

  implicit lazy val formats: OFormat[PublishedAfa] = Json.format[PublishedAfa]
}
