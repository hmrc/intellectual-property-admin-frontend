/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.CompanyApplyingIsRightsHolder
import play.api.data.FormError

class CompanyApplyingIsRightsHolderFormProviderSpec extends OptionFieldBehaviours {

  val form = new CompanyApplyingIsRightsHolderFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "companyApplyingIsRightsHolder.error.required"

    behave like optionsField[CompanyApplyingIsRightsHolder](
      form,
      fieldName,
      validValues  = CompanyApplyingIsRightsHolder.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
