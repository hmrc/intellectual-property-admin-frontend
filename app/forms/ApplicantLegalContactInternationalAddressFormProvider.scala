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
import models.InternationalAddress
import play.api.i18n.Messages
import utils.CommonHelpers.{regexErrorKey, rejectXssChars}

class ApplicantLegalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(implicit messages: Messages): Form[InternationalAddress] = Form(
    mapping(
      "line1"    ->
        text("applicantLegalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line1.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactInternationalAddress.line1")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line2.length"))
            .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactInternationalAddress.line2"))
        ),
      "town"     ->
        text("applicantLegalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.town.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactInternationalAddress.town")),
      "country"  ->
        text("applicantLegalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.country.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "applicantLegalContactInternationalAddress.country")),
      "postCode" ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.postCode.length"))
            .verifying(
              regexpDynamic(
                rejectXssChars,
                regexErrorKey,
                "applicantLegalContactInternationalAddress.postCode.noOption"
              )
            )
        )
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
