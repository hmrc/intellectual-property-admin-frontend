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
import models.TechnicalContact
import play.api.data.Form
import play.api.data.Forms.mapping

class WhoIsSecondaryTechnicalContactFormProvider extends Mappings {

  val nameEmailLimit: Int    = 200
  val phonesLimit: Int       = 100
  val rejectXssChars: String = """^[^<>"&]*$"""

  def apply(): Form[TechnicalContact] = Form(
    mapping(
      "name"        -> text("whoIsSecondaryTechnicalContact.error.name.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryTechnicalContact.error.name.length"))
        .verifying(regexp(rejectXssChars, "")),
      "companyName" -> text("whoIsSecondaryTechnicalContact.error.companyName.required")
        .verifying(maxLength(nameEmailLimit, "whoIsSecondaryTechnicalContact.error.companyName.length"))
        .verifying(regexp(rejectXssChars, "")),
      "telephone"   -> text("whoIsSecondaryTechnicalContact.error.telephone.required")
        .verifying(maxLength(phonesLimit, "whoIsSecondaryTechnicalContact.error.telephone.length"))
        .verifying(regexp(rejectXssChars, "")),
      "email"       -> email.verifying(validateEmail)
    )(TechnicalContact.apply)(TechnicalContact.unapply)
  )
}
