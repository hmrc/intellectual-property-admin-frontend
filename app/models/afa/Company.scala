/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import models.{Address, CompanyApplyingIsRightsHolder}
import play.api.libs.json.{Json, OFormat}

final case class Company(
                          name: String,
                          acronym: Option[String],
                          applicantAddressUkBased: Boolean,
                          applicantContactAddress: Address,
                          applicantType: CompanyApplyingIsRightsHolder
                        )

object Company {

  implicit lazy val format: OFormat[Company] = Json.format[Company]
}
