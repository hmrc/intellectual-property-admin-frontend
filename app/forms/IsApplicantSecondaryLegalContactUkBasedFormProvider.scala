/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IsApplicantSecondaryLegalContactUkBasedFormProvider @Inject() extends Mappings {

  def apply(applicantSecondaryLegalContactName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isApplicantSecondaryLegalContactUkBased.error.required",
          "isApplicantSecondaryLegalContactUkBased.error.required", applicantSecondaryLegalContactName)
    )
}
