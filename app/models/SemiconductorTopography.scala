/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

final case class SemiconductorTopography(description: String) extends IpRight {

  def rightType: String = "semiconductorTopography"
}

object SemiconductorTopography {

  implicit lazy val reads: Reads[SemiconductorTopography] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "semiconductorTopography") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `semiconductorTopography`"))
        }
    }.andKeep((__ \ "description").read[String]
      .map(SemiconductorTopography(_)))
  }
}
