/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import play.api.libs.json._

final case class SemiconductorTopography(description: String) extends IpRight {

  def rightsType: String = "semiconductorTopography"
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

  implicit lazy val writes: Writes[SemiconductorTopography] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "description").write[String]
    )(a => (a.rightsType, a.description))
  }
}
