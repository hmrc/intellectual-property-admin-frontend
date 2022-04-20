/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import java.time.LocalDate

import play.api.libs.json._

final case class Design(
                         registrationNumber: String,
                         registrationEnd: LocalDate,
                         description: String
                       ) extends IpRight {

  def rightsType: String = "design"
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

  implicit lazy val writes: Writes[Design] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "registrationNumber").write[String] and
      (__ \ "registrationEnd").write[LocalDate] and
      (__ \ "description").write[String]
    )(a => (a.rightsType, a.registrationNumber, a.registrationEnd, a.description))
  }
}
