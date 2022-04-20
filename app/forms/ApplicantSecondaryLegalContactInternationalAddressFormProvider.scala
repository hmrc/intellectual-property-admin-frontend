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

class ApplicantSecondaryLegalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1" ->
        text("applicantSecondaryLegalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.line2.length"))),
      "town" ->
        text("applicantSecondaryLegalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.town.length")),
      "country" ->
        text("applicantSecondaryLegalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.country.length")),
      "postCode" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.postCode.length")))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
