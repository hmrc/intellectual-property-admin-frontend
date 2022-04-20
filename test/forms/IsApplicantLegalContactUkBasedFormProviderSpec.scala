/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsApplicantLegalContactUkBasedFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isApplicantLegalContactUkBased.error.required"

  val rightsHolderContactName = "foo"

  val form = new IsApplicantLegalContactUkBasedFormProvider()(rightsHolderContactName)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey, Seq(rightsHolderContactName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey, Seq(rightsHolderContactName))
    )
  }
}
