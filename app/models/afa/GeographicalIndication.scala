/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import play.api.libs.json._

final case class GeographicalIndication(description: String) extends IpRight {

  def rightsType: String = "geographicalIndication"
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
        (__ \ "description").read[String] map GeographicalIndication.apply
    )
  }

  implicit lazy val writes: Writes[GeographicalIndication] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "description").write[String]
    )(a => (a.rightsType, a.description))
  }
}
