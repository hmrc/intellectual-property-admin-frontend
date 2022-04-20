/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.IpRightsType

class IpRightsTypeFormProvider @Inject() extends Mappings {

  def apply(): Form[IpRightsType] =
    Form(
      "value" -> enumerable[IpRightsType]("ipRightsType.error.required")
    )
}
