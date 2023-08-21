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

class SecondaryTechnicalContactInternationalAddressFormProvider @Inject() extends Mappings {

  val maxLength: Int         = 100
  val rejectXssChars: String = """^[^<>"&]*$"""

  val regexErrorKey: String = "regex.error"

  def apply(implicit messages: Messages): Form[InternationalAddress] = Form(
    mapping(
      "line1"    -> text("secondaryTechnicalContactInternationalAddress.error.line1.required")
        .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.line1.length"))
        .verifying(
          regexpDynamic(
            rejectXssChars,
            regexErrorKey,
            "secondaryTechnicalContactInternationalAddress.line1"
          )
        ),
      "line2"    -> optional(
        Forms.text
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.line2.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactInternationalAddress.line2")
          )
      ),
      "town"     -> text("secondaryTechnicalContactInternationalAddress.error.town.required")
        .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.town.length"))
        .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactInternationalAddress.town")),
      "country"  -> text("secondaryTechnicalContactInternationalAddress.error.country.required")
        .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.country.length"))
        .verifying(
          regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactInternationalAddress.country")
        ),
      "postCode" -> optional(
        Forms.text
          .verifying(maxLength(maxLength, "secondaryTechnicalContactInternationalAddress.error.postCode.length"))
          .verifying(
            regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactInternationalAddress.postCode")
          )
      )
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
