/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

final case class GeographicalIndication(description: String) extends IpRight {

  def rightType: String = "geographicalIndication"
}

object GeographicalIndication {

  implicit lazy val reads: Reads[GeographicalIndication] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "geographicalIndication") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `geographicalIndication`"))
        }
    }.andKeep(
      (__ \ "description").read[String].map(GeographicalIndication(_))
    )
  }
}
