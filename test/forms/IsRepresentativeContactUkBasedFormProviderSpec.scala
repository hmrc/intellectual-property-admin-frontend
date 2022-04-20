/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class IsRepresentativeContactUkBasedFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "isRepresentativeContactUkBased.error.required"

  val rightsHolderContactName = "foo"

  val form = new IsRepresentativeContactUkBasedFormProvider()(rightsHolderContactName)

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
