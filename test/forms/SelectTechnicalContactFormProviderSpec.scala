/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.ContactOptions
import play.api.data.FormError

class SelectTechnicalContactFormProviderSpec extends OptionFieldBehaviours {

  val requiredKey = "selectTechnicalContact.error.required"

  val form = new SelectTechnicalContactFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like optionsField[ContactOptions](
      form,
      fieldName,
      validValues  = ContactOptions.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

}
