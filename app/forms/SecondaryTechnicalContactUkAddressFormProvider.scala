/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.UkAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}

class SecondaryTechnicalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("secondaryTechnicalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.line2.length"))),
      "town" ->
        text("secondaryTechnicalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.town.length")),
      "county" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.county.length"))),
      "postCode" ->
        text("secondaryTechnicalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "secondaryTechnicalContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
