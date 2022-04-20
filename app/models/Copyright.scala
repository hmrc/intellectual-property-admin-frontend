/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

final case class Copyright (description: String) extends IpRight {

def rightType: String = "copyright"
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
}
