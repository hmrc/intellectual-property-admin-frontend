/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json._

final case class Design(
                         registrationNumber: String,
                         registrationEnd: LocalDate,
                         description: String
                       ) extends IpRight {

  def rightType: String = "design"
}

object Design {

  implicit lazy val reads: Reads[Design] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "design") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `design`"))
        }
    }.andKeep(
      (
        (__ \ "registrationNumber").read[String] and
        (__ \ "registrationEnd").read[LocalDate] and
        (__ \ "description").read[String]
      )(Design(_, _, _))
    )
  }
}
