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
import models.WhoIsSecondaryLegalContact
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import utils.CommonHelpers.{regexErrorKey, rejectXssChars}

class WhoIsSecondaryLegalContactFormProvider extends Mappings {

  val nameEmailLimit: Int = 200
  val phonesLimit: Int    = 100

  def apply(implicit messages: Messages): Form[WhoIsSecondaryLegalContact] = Form(
    mapping(
      "companyName" -> text("whoIsSecondaryLegalContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryLegalContact.error.companyName.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "whoIsSecondaryLegalContact.companyName.label")),
      "name"        -> text("whoIsSecondaryLegalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryLegalContact.error.name.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "whoIsSecondaryLegalContact.name.label")),
      "telephone"   -> text("whoIsSecondaryLegalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "whoIsSecondaryLegalContact.error.telephone.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "whoIsSecondaryLegalContact.telephone.label")),
      "email"       -> email.verifying(validateEmail)
    )(WhoIsSecondaryLegalContact.apply)(WhoIsSecondaryLegalContact.unapply)
  )
}
