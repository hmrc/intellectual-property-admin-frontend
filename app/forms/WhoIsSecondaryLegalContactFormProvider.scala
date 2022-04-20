/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import models.WhoIsSecondaryLegalContact
import play.api.data.Form
import play.api.data.Forms.mapping

class WhoIsSecondaryLegalContactFormProvider extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int = 100

  def apply(): Form[WhoIsSecondaryLegalContact] = Form(
      mapping(
        "companyName" -> text("whoIsSecondaryLegalContact.error.companyName.required")
          .verifying(maxLength(nameEmailLimit, "whoIsSecondaryLegalContact.error.companyName.length")),

      "name" -> text("whoIsSecondaryLegalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryLegalContact.error.name.length")),

      "telephone" -> text("whoIsSecondaryLegalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "whoIsSecondaryLegalContact.error.telephone.length")),

      "email" -> email.verifying(validateEmail)
      )(WhoIsSecondaryLegalContact.apply)(WhoIsSecondaryLegalContact.unapply)
    )
}
