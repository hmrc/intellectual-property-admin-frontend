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
import models.UkAddress
import play.api.i18n.Messages
import utils.CommonHelpers.{regexErrorKey, rejectXssChars}

class ApplicantLegalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  def apply(implicit messages: Messages): Form[UkAddress] = Form(
    mapping(
      "line1"    ->
        text("applicantLegalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.line1.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactUkAddress.line1")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.line2.length"))
            .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactUkAddress.line2"))
        ),
      "town"     ->
        text("applicantLegalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.town.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactUkAddress.town")),
      "county"   ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "applicantLegalContactUkAddress.error.county.length"))
            .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactUkAddress.county"))
        ),
      "postCode" ->
        text("applicantLegalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "applicantLegalContactUkAddress.error.postCode.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactUkAddress.postCode"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
