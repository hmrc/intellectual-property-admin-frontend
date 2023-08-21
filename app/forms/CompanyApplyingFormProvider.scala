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
import models.CompanyApplying
import play.api.data.Forms._
import play.api.data.{Form, Forms}
import play.api.i18n.Messages

import javax.inject.Inject

class CompanyApplyingFormProvider @Inject() () extends Mappings {

  val maxLength: Int         = 200
  val rejectXssChars: String = """^[^<>"&]*$"""

  val regexErrorKey: String = "regex.error"

  def apply(implicit messages: Messages): Form[CompanyApplying] = Form(
    mapping(
      "companyName"    -> text("companyApplying.error.companyName.required")
        .verifying(maxLength(maxLength, "companyApplying.error.companyName.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "companyApplying.companyName")),
      "companyAcronym" -> optional(
        Forms.text
          .verifying(maxLength(maxLength, "companyApplying.error.companyAcronym.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "companyApplying.acronym.checkYourAnswersLabel")
          )
      )
    )(CompanyApplying.apply)(CompanyApplying.unapply)
  )
}
