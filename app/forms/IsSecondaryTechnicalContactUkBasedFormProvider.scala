/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class IsSecondaryTechnicalContactUkBasedFormProvider @Inject() extends Mappings {

  def apply(secondaryTechnicalContactName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isSecondaryTechnicalContactUkBased.error.required",
          "isSecondaryTechnicalContactUkBased.error.required", secondaryTechnicalContactName)
    )
}
