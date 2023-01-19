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

class TechnicalContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1"    ->
        text("technicalContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "technicalContactUkAddress.error.line1.length")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "technicalContactUkAddress.error.line2.length"))
        ),
      "town"     ->
        text("technicalContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "technicalContactUkAddress.error.town.length")),
      "county"   ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "technicalContactUkAddress.error.county.length"))
        ),
      "postCode" ->
        text("technicalContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "technicalContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
