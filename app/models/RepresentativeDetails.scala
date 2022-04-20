/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, OFormat}

final case class RepresentativeDetails(
                                        contactName: String,
                                        companyName: String,
                                        phone: String,
                                        email: String,
                                        roleOrPosition: Option[String]
                                      ) {
  def getAsLegal: ApplicantLegalContact = {
    ApplicantLegalContact(companyName, contactName, phone, None, email)
  }
}

object RepresentativeDetails {

  implicit lazy val formats: OFormat[RepresentativeDetails] = Json.format[RepresentativeDetails]
}
