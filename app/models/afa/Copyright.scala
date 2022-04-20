/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import play.api.libs.json._

final case class Copyright(description: String) extends IpRight {

  def rightsType: String = "copyright"
}

object Copyright {

  implicit lazy val reads: Reads[Copyright] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "copyright") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `copyright`"))
        }
    }.andKeep((__ \ "description").read[String]
      .map(Copyright(_)))
  }

  implicit lazy val writes: Writes[Copyright] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "description").write[String]
    )(a => (a.rightsType, a.description))
  }
}
