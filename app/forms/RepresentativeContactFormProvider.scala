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

import forms.mappings.Mappings

import javax.inject.Inject
import models.RepresentativeDetails
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import play.api.i18n.Messages

class RepresentativeContactFormProvider @Inject() extends Mappings {

  val nameEmailLimit: Int    = 200
  val phoneRoleLimit: Int    = 100
  val rejectXssChars: String = """^[^<>"&]*$"""

  val regexErrorKey: String = "regex.error"

  def apply(messages: Messages): Form[RepresentativeDetails] = Form(
    mapping(
      "name"        -> text("representativeContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "representativeContact.error.name.length"))
        .verifying(regexpArgs(rejectXssChars, regexErrorKey, messages("representativeContact.name.label"))),
      "companyName" -> text("representativeContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "representativeContact.error.companyName.length"))
        .verifying(regexpArgs(rejectXssChars, regexErrorKey, messages("representativeContact.companyName.label"))),
      "telephone"   -> text("representativeContact.error.telephone.required")
        .verifying(maxLength(phoneRoleLimit, "representativeContact.error.telephone.length"))
        .verifying(regexpArgs(rejectXssChars, regexErrorKey, messages("representativeContact.telephone.label"))),
      "email"       -> email.verifying(validateEmail),
      "role"        -> optional(
        Forms.text
          .verifying(maxLength(phoneRoleLimit, "representativeContact.error.role.length"))
          .verifying(
            regexpArgs(rejectXssChars, regexErrorKey, messages("representativeContact.role.checkYourAnswersLabel"))
          )
      )
    )(RepresentativeDetails.apply)(RepresentativeDetails.unapply)
  )
}
