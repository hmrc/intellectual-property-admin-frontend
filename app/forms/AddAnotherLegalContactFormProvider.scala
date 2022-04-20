/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class AddAnotherLegalContactFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("addAnotherLegalContact.error.required", "addAnotherLegalContact.error.required")
    )
}
