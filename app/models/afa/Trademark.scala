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

import java.time.LocalDate

import models.NiceClassId
import play.api.libs.json._

final case class Trademark(
                            registrationNumber: String,
                            registrationEnd: LocalDate,
                            brand: Option[String],
                            description: String,
                            niceClasses: Seq[NiceClassId]
                          ) extends IpRight {

  def rightsType: String = "trademark"
}

object Trademark {

  implicit lazy val reads: Reads[Trademark] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "trademark") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `trademark`"))
        }
    }.andKeep(
      (
        (__ \ "registrationNumber").read[String] and
        (__ \ "registrationEnd").read[LocalDate] and
        (__ \ "brand").readNullable[String] and
        (__ \ "description").read[String] and
        (__ \ "niceClasses").read[Seq[NiceClassId]]
      )(Trademark(_, _, _, _, _))
    )
  }

  implicit lazy val writes: Writes[Trademark] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
      (__ \ "registrationNumber").write[String] and
      (__ \ "registrationEnd").write[LocalDate] and
      (__ \ "brand").writeNullable[String] and
      (__ \ "description").write[String] and
      (__ \ "niceClasses").write[Seq[NiceClassId]]
    )(a => (a.rightsType, a.registrationNumber, a.registrationEnd, a.brand, a.description, a.niceClasses))
  }
}
