/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IsCompanyApplyingUkBasedFormProvider @Inject() extends Mappings {

  def apply(CompanyApplyingName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isCompanyApplyingUkBased.error.required", "isCompanyApplyingUkBased.error.required", CompanyApplyingName)
    )
}
