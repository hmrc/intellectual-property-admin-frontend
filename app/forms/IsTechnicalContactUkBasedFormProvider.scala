/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IsTechnicalContactUkBasedFormProvider @Inject() extends Mappings {

  def apply(infringementContactName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isTechnicalContactUkBased.error.required", "isTechnicalContactUkBased.error.required", infringementContactName)
    )
}
