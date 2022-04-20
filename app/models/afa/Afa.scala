/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import java.time.LocalDate
import models.AfaId
import play.api.libs.json._

final case class InitialAfa(id: AfaId,
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
                            endDate: LocalDate,
                            ipRights: Seq[IpRight],
                            representativeContact: RepresentativeContact,
                            isRestrictedHandling: Option[Boolean]) {

  def isExOfficio: Boolean = exOfficio.isDefined
}

object InitialAfa {

  implicit lazy val formats: OFormat[InitialAfa] = Json.format[InitialAfa]
}
