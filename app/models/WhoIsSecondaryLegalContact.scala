/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class WhoIsSecondaryLegalContact (
                               companyName: String,
                               contactName: String,
                               contactTelephone: String,
                               contactEmail: String
                             )

object WhoIsSecondaryLegalContact {
  implicit val format: OFormat[WhoIsSecondaryLegalContact] = Json.format[WhoIsSecondaryLegalContact]
}
