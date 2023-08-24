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
import models.UkAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}
import play.api.i18n.Messages
import utils.CommonHelpers.{regexErrorKey, rejectXssChars}

class SecondaryTechnicalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  def apply(implicit messages: Messages): Form[UkAddress] = Form(
    mapping(
      "line1"    ->
        text("secondaryTechnicalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.line1.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactUkAddress.line1")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.line2.length"))
            .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactUkAddress.line2"))
        ),
      "town"     ->
        text("secondaryTechnicalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.town.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactUkAddress.town")),
      "county"   ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "secondaryTechnicalContactUkAddress.error.county.length"))
            .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactUkAddress.county"))
        ),
      "postCode" ->
        text("secondaryTechnicalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "secondaryTechnicalContactUkAddress.error.postCode.length"))
          .verifying(regexpDynamic(rejectXssChars, regexErrorKey, "secondaryTechnicalContactUkAddress.postCode"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
