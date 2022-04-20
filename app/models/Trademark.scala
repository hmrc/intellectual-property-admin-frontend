/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json._

final case class Trademark(
                            registrationNumber: String,
                            registrationEnd: LocalDate,
                            brand: Option[String],
      description: String,
      niceClasses: Seq[NiceClassId]
    ) extends IpRight {

      def rightType: String = "trademark"
    }

    object Trademark {

      implicit lazy val reads: Reads[Trademark] = {

        import play.api.libs.functional.syntax._

    val readsWithDescriptionAndBrand: Reads[Trademark] = (
      (__ \ "registrationNumber").read[String] and
      (__ \ "registrationEnd").read[LocalDate] and
      (__ \ "descriptionWithBrand" \ "brand").read[String] and
      (__ \ "descriptionWithBrand" \ "description").read[String] and
      (__ \ "niceClasses").read[Seq[NiceClassId]]
    )((regNum, regEnd, brand, desc, niceClasses) => Trademark(regNum, regEnd, Some(brand), desc, niceClasses))

    (__ \ "rightsType").read[String].flatMap[String] {
      t =>
        if (t == "trademark") {
          Reads(_ => JsSuccess(t))
        } else {
          Reads(_ => JsError("rightsType must be `trademark`"))
        }
    }.andKeep(readsWithDescriptionAndBrand)
  }
}
