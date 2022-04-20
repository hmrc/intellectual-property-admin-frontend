/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class PlantVariety (description: String) extends IpRight {

  def rightType: String = "plantVariety"
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
}
