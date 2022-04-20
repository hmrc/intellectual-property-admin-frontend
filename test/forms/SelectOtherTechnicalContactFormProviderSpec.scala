/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.ContactOptions
import play.api.data.FormError

class SelectOtherTechnicalContactFormProviderSpec extends OptionFieldBehaviours {

  val requiredKey = "selectOtherTechnicalContact.error.required"

  val form = new SelectOtherTechnicalContactFormProvider()()

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
