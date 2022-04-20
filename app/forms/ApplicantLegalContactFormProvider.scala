/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.ApplicantLegalContact

class ApplicantLegalContactFormProvider @Inject() extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int = 100

   def apply(): Form[ApplicantLegalContact] = Form(
     mapping(
       "companyName" -> text("applicantLegalContact.error.companyName.required")
         .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.companyName.length")),

       "name" -> text("applicantLegalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.name.length")),

      "telephone" -> text("applicantLegalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "applicantLegalContact.error.telephone.length")),

       "otherTelephone" -> optional(Forms.text
         .verifying(maxLength(phonesLimit, "applicantLegalContact.error.otherTelephone.length"))),

      "email" -> email.verifying(validateEmail)
    )(ApplicantLegalContact.apply)(ApplicantLegalContact.unapply)
   )
 }
