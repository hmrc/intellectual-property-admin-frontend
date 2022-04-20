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

class CompanyApplyingUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1" ->
        text("companyApplyingUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "companyApplyingUkAddress.error.line1.length")),
      "line2" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "companyApplyingUkAddress.error.line2.length"))),
      "town" ->
        text("companyApplyingUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "companyApplyingUkAddress.error.town.length")),
      "county" ->
        optional(Forms.text
          .verifying(maxLength(linesMaxLength, "companyApplyingUkAddress.error.county.length"))),
      "postCode" ->
        text("companyApplyingUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "companyApplyingUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
