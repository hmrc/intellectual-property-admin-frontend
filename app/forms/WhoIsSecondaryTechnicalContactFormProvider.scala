/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import models.TechnicalContact
import play.api.data.Form
import play.api.data.Forms.mapping

class WhoIsSecondaryTechnicalContactFormProvider extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int = 100

  def apply(): Form[TechnicalContact] = Form(
    mapping(
      "name" -> text("whoIsSecondaryTechnicalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryTechnicalContact.error.name.length")),

      "companyName" -> text("whoIsSecondaryTechnicalContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryTechnicalContact.error.companyName.length")),

      "telephone" -> text("whoIsSecondaryTechnicalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "whoIsSecondaryTechnicalContact.error.telephone.length")),

      "email" -> email.verifying(validateEmail)
    )(TechnicalContact.apply)(TechnicalContact.unapply)
  )
}
