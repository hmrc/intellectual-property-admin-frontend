/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms.testonly

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class InsertSingleAfaFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("insertSingleAfa.error.required")
    )
}
