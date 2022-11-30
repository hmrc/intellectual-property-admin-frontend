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

package models

import java.time.LocalDate

import play.api.libs.json._

final case class Design(
  registrationNumber: String,
  registrationEnd: LocalDate,
  description: String
) extends IpRight {

  def rightType: String = "design"
}

object Design {

  implicit lazy val reads: Reads[Design] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType")
      .read[String]
      .flatMap[String] { t =>
        if (t == "design") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `design`"))
        }
      }
      .andKeep(
        (
          (__ \ "registrationNumber").read[String] and
            (__ \ "registrationEnd").read[LocalDate] and
            (__ \ "description").read[String]
        )(Design(_, _, _))
      )
  }
}
