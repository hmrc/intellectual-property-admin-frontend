/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import java.time.LocalDate

import models.NiceClassId
import play.api.libs.json._

final case class Trademark(
                            registrationNumber: String,
                            registrationEnd: LocalDate,
                            brand: Option[String],
                            description: String,
                            niceClasses: Seq[NiceClassId]
                          ) extends IpRight {

  def rightsType: String = "trademark"
}

object Trademark {

  implicit lazy val reads: Reads[Trademark] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "trademark") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `trademark`"))
        }
    }.andKeep(
      (
        (__ \ "registrationNumber").read[String] and
        (__ \ "registrationEnd").read[LocalDate] and
        (__ \ "brand").readNullable[String] and
        (__ \ "description").read[String] and
        (__ \ "niceClasses").read[Seq[NiceClassId]]
      )(Trademark(_, _, _, _, _))
    )
  }

  implicit lazy val writes: Writes[Trademark] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "registrationNumber").write[String] and
      (__ \ "registrationEnd").write[LocalDate] and
      (__ \ "brand").writeNullable[String] and
      (__ \ "description").write[String] and
      (__ \ "niceClasses").write[Seq[NiceClassId]]
    )(a => (a.rightsType, a.registrationNumber, a.registrationEnd, a.brand, a.description, a.niceClasses))
  }
}
