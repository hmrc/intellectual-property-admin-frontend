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
import models.InternationalAddress

class ApplicantLegalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int = 100

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1"    ->
        text("applicantLegalContactInternationalAddress.error.line1.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line1.length")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.line2.length"))
        ),
      "town"     ->
        text("applicantLegalContactInternationalAddress.error.town.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.town.length")),
      "country"  ->
        text("applicantLegalContactInternationalAddress.error.country.required")
          .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.country.length")),
      "postCode" ->
        optional(
          Forms.text
            .verifying(maxLength(maxLength, "applicantLegalContactInternationalAddress.error.postCode.length"))
        )
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
