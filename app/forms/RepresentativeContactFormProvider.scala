/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms


import forms.mappings.Mappings
import javax.inject.Inject
import models.RepresentativeDetails
import play.api.data.{Form, Forms}
import play.api.data.Forms._

class RepresentativeContactFormProvider @Inject() extends Mappings {

  val nameEmailLimit: Int = 200
  val phoneRoleLimit: Int = 100

  def apply(): Form[RepresentativeDetails] = Form(
    mapping(

      "name" -> text("representativeContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "representativeContact.error.name.length")),

      "companyName" -> text("representativeContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "representativeContact.error.companyName.length")),

      "telephone" -> text("representativeContact.error.telephone.required")
        .verifying(maxLength(phoneRoleLimit, "representativeContact.error.telephone.length")),

      "email" -> email.verifying(validateEmail),

      "role" -> optional(Forms.text
        .verifying(maxLength(phoneRoleLimit, "representativeContact.error.role.length")))

    )(RepresentativeDetails.apply)(RepresentativeDetails.unapply)
  )
}
