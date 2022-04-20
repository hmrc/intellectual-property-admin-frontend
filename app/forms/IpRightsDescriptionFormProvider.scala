/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IpRightsDescriptionFormProvider @Inject() extends Mappings {

  val maxLength:Int = 1000

  def apply(): Form[String] =
    Form(
      "value" -> text("ipRightsDescription.error.required")
        .verifying(maxLength(maxLength, "ipRightsDescription.error.length"))
    )
}
