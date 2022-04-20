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

class RepresentativeContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("representativeContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.line2.length"))),
      "town" ->
        text("representativeContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.town.length")),
      "county" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.county.length"))),
      "postCode" ->
        text("representativeContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "representativeContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
