/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, OFormat}

final case class TechnicalContact(
                             companyName: String,
                             contactName: String,
                             contactTelephone: String,
                             contactEmail: String
                           )

object TechnicalContact {
  implicit val formats: OFormat[TechnicalContact] = Json.format[TechnicalContact]
}
