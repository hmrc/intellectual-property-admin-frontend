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
import models.TechnicalContact
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import utils.CommonHelpers.{errorKeyXSSNoAmpersand, regexErrorKey, regexXSSNoAmpersand, rejectXssChars}

class WhoIsTechnicalContactFormProvider @Inject() extends Mappings {

  val nameCompanyEmailLimit: Int = 200
  val phonesLimit: Int           = 100

  def apply(implicit messages: Messages): Form[TechnicalContact] = Form(
    mapping(
      "contactName"      -> text("whoIsTechnicalContact.error.contactName.required")
        .verifying(maxLength(nameCompanyEmailLimit, "whoIsTechnicalContact.error.contactName.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "whoIsTechnicalContact.contactName")),
      "companyName"      -> text("whoIsTechnicalContact.error.companyName.required")
        .verifying(maxLength(nameCompanyEmailLimit, "whoIsTechnicalContact.error.companyName.length"))
        .verifying(regexpDynamic(regexXSSNoAmpersand, errorKeyXSSNoAmpersand, "whoIsTechnicalContact.companyName")),
      "contactTelephone" -> text("whoIsTechnicalContact.error.contactTelephone.required")
        .verifying(maxLength(phonesLimit, "whoIsTechnicalContact.error.contactTelephone.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "whoIsTechnicalContact.contactTelephone")),
      "contactEmail"     -> email.verifying(validateEmail)
    )(TechnicalContact.apply)(TechnicalContact.unapply)
  )
}
