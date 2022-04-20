/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import models.Address
import play.api.libs.json.{Json, OFormat}

final case class RepresentativeContact(
                                        contactName: String,
                                        companyName: String,
                                        phone: String,
                                        email: String,
                                        roleOrPosition: Option[String],
                                        address: Address,
                                        evidenceOfPowerToAct: Option[Boolean]
                                      )

object RepresentativeContact {

  implicit lazy val formats: OFormat[RepresentativeContact] = Json.format[RepresentativeContact]
}
