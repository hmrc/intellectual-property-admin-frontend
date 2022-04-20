/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class AddAnotherTechnicalContactFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("addAnotherTechnicalContact.error.required", "addAnotherTechnicalContact.error.required")
    )
}
