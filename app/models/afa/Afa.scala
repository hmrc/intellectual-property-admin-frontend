/*
 * Copyright 2023 HM Revenue & Customs
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

package models.afa

import java.time.LocalDate
import models.AfaId
import play.api.libs.json._

final case class InitialAfa(
  id: AfaId,
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
  isRestrictedHandling: Option[Boolean]
) {

  def isExOfficio: Boolean = exOfficio.isDefined
}

object InitialAfa {

  implicit lazy val formats: OFormat[InitialAfa] = Json.format[InitialAfa]
}
