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

class ApplicantSecondaryLegalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("applicantSecondaryLegalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "applicantSecondaryLegalContactUkAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "applicantSecondaryLegalContactUkAddress.error.line2.length"))),
      "town" ->
        text("applicantSecondaryLegalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "applicantSecondaryLegalContactUkAddress.error.town.length")),
      "county" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "applicantSecondaryLegalContactUkAddress.error.county.length"))),
      "postCode" ->
        text("applicantSecondaryLegalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "applicantSecondaryLegalContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
