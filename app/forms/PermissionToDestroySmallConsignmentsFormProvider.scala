/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class PermissionToDestroySmallConsignmentsFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("permissionToDestroySmallConsignments.error.required", "permissionToDestroySmallConsignments.error.required")
    )
}
