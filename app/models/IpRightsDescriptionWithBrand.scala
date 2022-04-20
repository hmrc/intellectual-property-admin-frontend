/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class IpRightsDescriptionWithBrand (brand: String, description: String)

object IpRightsDescriptionWithBrand {
  implicit val format = Json.format[IpRightsDescriptionWithBrand]
}
