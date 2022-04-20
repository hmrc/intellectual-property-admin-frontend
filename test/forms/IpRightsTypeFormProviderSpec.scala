/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.IpRightsType
import play.api.data.FormError

class IpRightsTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new IpRightsTypeFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "ipRightsType.error.required"

    behave like optionsField[IpRightsType](
      form,
      fieldName,
      validValues  = IpRightsType.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
