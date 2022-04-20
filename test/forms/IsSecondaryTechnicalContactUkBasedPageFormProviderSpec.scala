/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsSecondaryTechnicalContactUkBasedPageFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isSecondaryTechnicalContactUkBased.error.required"

  val secondTechnicalContactName = "foo"

  val form = new IsSecondaryTechnicalContactUkBasedFormProvider()(secondTechnicalContactName)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey, Seq(secondTechnicalContactName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey, Seq(secondTechnicalContactName))
    )
  }
}
