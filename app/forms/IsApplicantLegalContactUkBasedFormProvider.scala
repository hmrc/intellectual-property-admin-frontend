/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IsApplicantLegalContactUkBasedFormProvider @Inject() extends Mappings {

  def apply(applicantLegalContactName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isApplicantLegalContactUkBased.error.required", "isApplicantLegalContactUkBased.error.required", applicantLegalContactName)
    )
}
