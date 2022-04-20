/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class CompanyApplying (
                             name: String,
                             acronym: Option[String])

object CompanyApplying {
  val name = "companyName"

  implicit val format = Json.format[CompanyApplying]
}
