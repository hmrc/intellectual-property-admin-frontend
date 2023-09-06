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
import models.InternationalAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}
import play.api.i18n.Messages
import utils.CommonHelpers.{regexErrorKey, rejectXssChars}

class ApplicantSecondaryLegalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(implicit messages: Messages): Form[InternationalAddress] = Form(
    mapping(
      "line1"    ->
        text("applicantSecondaryLegalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.line1.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "applicantSecondaryLegalContactInternationalAddress.line1")
          ),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.line2.length"))
            .verifying(
              regexpDynamic(rejectXssChars, regexErrorKey, "applicantSecondaryLegalContactInternationalAddress.line2")
            )
        ),
      "town"     ->
        text("applicantSecondaryLegalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.town.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "applicantSecondaryLegalContactInternationalAddress.town")
          ),
      "country"  ->
        text("applicantSecondaryLegalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.country.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "applicantSecondaryLegalContactInternationalAddress.country")
          ),
      "postCode" ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantSecondaryLegalContactInternationalAddress.error.postCode.length"))
            .verifying(
              regexpDynamic(
                rejectXssChars,
                regexErrorKey,
                "applicantSecondaryLegalContactInternationalAddress.postCode.noOption"
              )
            )
        )
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
