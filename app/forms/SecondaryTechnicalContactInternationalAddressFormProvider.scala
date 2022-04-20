/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.InternationalAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}

class SecondaryTechnicalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1" ->
        text("secondaryTechnicalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.line2.length"))),
      "town" ->
        text("secondaryTechnicalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.town.length")),
      "country" ->
        text("secondaryTechnicalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.country.length")),
      "postCode" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.postCode.length")))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
