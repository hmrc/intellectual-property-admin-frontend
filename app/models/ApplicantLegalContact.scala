/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class ApplicantLegalContact(
                                  companyName: String,
                                  name: String,
                                  telephone: String,
                                  otherTelephone: Option[String],
                                  email: String
                                ) {
  def getAsTechnical: TechnicalContact = {
    TechnicalContact(companyName, name, telephone, email)
  }
}

object ApplicantLegalContact {

  implicit val format: OFormat[ApplicantLegalContact] = Json.format[ApplicantLegalContact]

}
