/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsApplicantSecondaryLegalContactUkBasedFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isApplicantSecondaryLegalContactUkBased.error.required"

  val secondaryLegalContactName = "foo"

  val form = new IsApplicantSecondaryLegalContactUkBasedFormProvider()(secondaryLegalContactName)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey, Seq(secondaryLegalContactName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey, Seq(secondaryLegalContactName))
    )
  }
}
