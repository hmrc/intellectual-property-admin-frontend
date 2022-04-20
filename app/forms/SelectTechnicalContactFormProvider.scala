/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.ContactOptions
import play.api.data.Form

class SelectTechnicalContactFormProvider @Inject() extends Mappings {

  def apply(): Form[ContactOptions] =
    Form(
      "value" -> enumerable[ContactOptions]("selectTechnicalContact.error.required")
    )
}

