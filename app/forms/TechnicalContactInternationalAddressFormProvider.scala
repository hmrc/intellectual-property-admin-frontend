/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.InternationalAddress

class TechnicalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1" ->
        text("technicalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "technicalContactInternationalAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "technicalContactInternationalAddress.error.line2.length"))),
      "town" ->
        text("technicalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "technicalContactInternationalAddress.error.town.length")),
      "country" ->
        text("technicalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "technicalContactInternationalAddress.error.country.length")),
      "postCode" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "technicalContactInternationalAddress.error.postCode.length")))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
