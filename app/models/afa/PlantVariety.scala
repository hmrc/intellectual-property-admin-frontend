/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import play.api.libs.json._

final case class PlantVariety(description: String) extends IpRight {

  def rightsType: String = "plantVariety"
}

object PlantVariety {

  implicit lazy val reads: Reads[PlantVariety] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "plantVariety") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `plantVariety`"))
        }
    }.andKeep((__ \ "description").read[String]
      .map(PlantVariety(_)))
  }

  implicit lazy val writes: Writes[PlantVariety] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "description").write[String]
    )(a => (a.rightsType, a.description))
  }
}
