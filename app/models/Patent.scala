/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json._

final case class Patent(
                         registrationNumber: String,
                         registrationEnd: LocalDate,
                         description: String
                       ) extends IpRight {

  def rightType: String = "patent"
}

object Patent {

  implicit lazy val reads: Reads[Patent] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "patent") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `patent`"))
        }
    }.andKeep(
      (
        (__ \ "registrationNumber").read[String] and
        (__ \ "registrationEnd").read[LocalDate] and
        (__ \ "description").read[String]
      )(Patent(_, _, _))
    )
  }

}
