/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.CompanyApplyingIsRightsHolder

class CompanyApplyingIsRightsHolderFormProvider @Inject() extends Mappings {

  def apply(): Form[CompanyApplyingIsRightsHolder] =
    Form(
      "value" -> enumerable[CompanyApplyingIsRightsHolder]("companyApplyingIsRightsHolder.error.required")
    )
}
