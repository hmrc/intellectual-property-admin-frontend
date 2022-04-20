/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class PermissionToDestroySmallConsignmentsFormProviderSpec extends BooleanFieldBehaviours {

  val errorKey = "permissionToDestroySmallConsignments.error.required"

  val form = new PermissionToDestroySmallConsignmentsFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, errorKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, errorKey)
    )
  }
}
