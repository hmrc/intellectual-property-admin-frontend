/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.CompanyApplying

class CompanyApplyingFormProvider @Inject() extends Mappings {

  val maxLength: Int = 200

   def apply(): Form[CompanyApplying] = Form(
     mapping(
      "companyName" -> text("companyApplying.error.companyName.required")
        .verifying(maxLength(maxLength, "companyApplying.error.companyName.length")),
      "companyAcronym" -> optional(Forms.text
        .verifying(maxLength(maxLength, "companyApplying.error.companyAcronym.length")))
    )(CompanyApplying.apply)(CompanyApplying.unapply)
   )
 }
