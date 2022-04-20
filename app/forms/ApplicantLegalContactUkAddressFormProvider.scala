/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.UkAddress

class ApplicantLegalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("applicantLegalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.line2.length"))),
      "town" ->
        text("applicantLegalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.town.length")),
      "county" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.county.length"))),
      "postCode" ->
        text("applicantLegalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "applicantLegalContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
