/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.i18n.Messages
import utils.CommonHelpers.{errorKeyXSSNoAmpersand, regexErrorKey, regexXSSNoAmpersand, rejectXssChars}

class ApplicantLegalContactFormProvider @Inject() extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int    = 100

  def apply(implicit messages: Messages): Form[ApplicantLegalContact] = Form(
    mapping(
      "companyName"    -> text("applicantLegalContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.companyName.length"))
        .verifying(
          regexpDynamic(regexXSSNoAmpersand, errorKeyXSSNoAmpersand, "applicantLegalContact.companyName.label")
        ),
      "name"           -> text("applicantLegalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "applicantLegalContact.error.name.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContact.name.label")),
      "telephone"      -> text("applicantLegalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "applicantLegalContact.error.telephone.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContact.telephone.label")),
      "otherTelephone" -> optional(
        Forms.text
          .verifying(maxLength(phonesLimit, "applicantLegalContact.error.otherTelephone.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContact.otherTelephone.label.noOption")
          )
      ),
      "email"          -> email.verifying(validateEmail)
    )(ApplicantLegalContact.apply)(ApplicantLegalContact.unapply)
  )
}
