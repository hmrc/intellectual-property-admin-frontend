/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.libs.json._

final case class SupplementaryProtectionCertificate(
  certificateType: String,
  registrationNumber: String,
  registrationEnd: LocalDate,
  description: String
) extends IpRight {

  def rightsType: String = "supplementaryProtectionCertificate"
}

object SupplementaryProtectionCertificate {

  implicit lazy val reads: Reads[SupplementaryProtectionCertificate] = {

    import play.api.libs.functional.syntax._

    (__ \ "rightsType")
      .read[String]
      .flatMap[String] { t =>
        if (t == "supplementaryProtectionCertificate") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `supplementaryProtectionCertificate`"))
        }
      }
      .andKeep(
        (
          (__ \ "certificateType").read[String] and
            (__ \ "registrationNumber").read[String] and
            (__ \ "registrationEnd").read[LocalDate] and
            (__ \ "description").read[String]
        )(SupplementaryProtectionCertificate(_, _, _, _))
      )
  }

  implicit lazy val writes: Writes[SupplementaryProtectionCertificate] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "rightsType").write[String] and
        (__ \ "certificateType").write[String] and
        (__ \ "registrationNumber").write[String] and
        (__ \ "registrationEnd").write[LocalDate] and
        (__ \ "description").write[String]
    )(a => (a.rightsType, a.certificateType, a.registrationNumber, a.registrationEnd, a.description))
  }
}
