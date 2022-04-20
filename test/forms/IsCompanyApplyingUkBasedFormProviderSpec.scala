/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsCompanyApplyingUkBasedFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isCompanyApplyingUkBased.error.required"

  val contactName = "foo"

  val form = new IsCompanyApplyingUkBasedFormProvider()(contactName)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey, Seq(contactName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey, Seq(contactName))
    )
  }
}
