/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import models.Address
import play.api.libs.json.{Json, OFormat}

final case class Contact(
                         companyName: String,
                         name: String,
                         phone: String,
                         otherPhone: Option[String],
                         email: String,
                         address: Address
                        )

object Contact {

  implicit lazy val formats: OFormat[Contact] = Json.format[Contact]
}
