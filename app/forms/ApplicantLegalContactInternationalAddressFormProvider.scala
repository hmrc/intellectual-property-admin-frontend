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

class ApplicantLegalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1" ->
        text("applicantLegalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line2.length"))),
      "town" ->
        text("applicantLegalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.town.length")),
      "country" ->
        text("applicantLegalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.country.length")),
      "postCode" ->
        optional(Forms.text
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.postCode.length")))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
