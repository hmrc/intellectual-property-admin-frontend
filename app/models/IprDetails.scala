/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, Reads}

final case class IprDetails(description: Option[String], rightsType: Option[IpRightsType], registrationNumber: Option[String])

object IprDetails {

  implicit val reads: Reads[IprDetails] = Json.reads[IprDetails]
}
