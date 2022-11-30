/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.afa

import play.api.libs.json._

final case class GeographicalIndication(description: String) extends IpRight {

  def rightsType: String = "geographicalIndication"
}

object GeographicalIndication {

  implicit lazy val reads: Reads[GeographicalIndication] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType")
      .read[String]
      .flatMap[String] { t =>
        if (t == "geographicalIndication") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `geographicalIndication`"))
        }
      }
      .andKeep(
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
