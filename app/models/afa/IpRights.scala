/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import models.IpRightsType
import play.api.libs.json.{Format, Json}

final case class IpRights(
                           rightsType: IpRightsType,
                           registrationNumber: String,
                           description: String,
                           niceClasses: Seq[String]
                         )

object IpRights {

  implicit lazy val formats: Format[IpRights] = Json.format[IpRights]
}
