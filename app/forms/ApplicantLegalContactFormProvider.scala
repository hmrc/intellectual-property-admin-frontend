/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.ApplicantLegalContact

class ApplicantLegalContactFormProvider @Inject() extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int    = 100

  def apply(): Form[ApplicantLegalContact] = Form(
    mapping(
      "companyName"    -> text("applicantLegalContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.companyName.length")),
      "name"           -> text("applicantLegalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.name.length")),
      "telephone"      -> text("applicantLegalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "applicantLegalContact.error.telephone.length")),
      "otherTelephone" -> optional(
        Forms.text
          .verifying(maxLength(phonesLimit, "applicantLegalContact.error.otherTelephone.length"))
      ),
      "email"          -> email.verifying(validateEmail)
    )(ApplicantLegalContact.apply)(ApplicantLegalContact.unapply)
  )
}
