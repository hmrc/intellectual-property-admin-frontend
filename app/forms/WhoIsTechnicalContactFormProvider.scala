/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.TechnicalContact
import play.api.data.Form
import play.api.data.Forms._

class WhoIsTechnicalContactFormProvider @Inject() extends Mappings {

  val nameCompanyEmailLimit: Int = 200
  val phonesLimit: Int = 100

  def apply(): Form[TechnicalContact] = Form(
    mapping(
      "contactName" -> text("whoIsTechnicalContact.error.contactName.required")
        .verifying(maxLength(nameCompanyEmailLimit, "whoIsTechnicalContact.error.contactName.length")),

      "companyName" -> text("whoIsTechnicalContact.error.companyName.required")
        .verifying(maxLength(nameCompanyEmailLimit, "whoIsTechnicalContact.error.companyName.length")),

      "contactTelephone" -> text("whoIsTechnicalContact.error.contactTelephone.required")
        .verifying(maxLength(phonesLimit, "whoIsTechnicalContact.error.contactTelephone.length")),

      "contactEmail" -> email.verifying(validateEmail)
    )(TechnicalContact.apply)(TechnicalContact.unapply)
  )
}
