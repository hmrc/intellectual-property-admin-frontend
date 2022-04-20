/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsTechnicalContactUkBasedPageFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isTechnicalContactUkBased.error.required"

  val infringementContactName = "foo"

  val form = new IsTechnicalContactUkBasedFormProvider()(infringementContactName)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey, Seq(infringementContactName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey, Seq(infringementContactName))
    )
  }
}
