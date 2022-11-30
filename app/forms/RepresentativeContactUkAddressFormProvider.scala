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

import forms.mappings.Mappings
import javax.inject.Inject
import models.UkAddress
import play.api.data.Forms._
import play.api.data.{Form, Forms}

class RepresentativeContactUkAddressFormProvider @Inject() extends Mappings {

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  def apply(): Form[UkAddress] = Form(
    mapping(
      "line1"    ->
        text("representativeContactUkAddress.error.line1.required")
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.line1.length")),
      "line2"    ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.line2.length"))
        ),
      "town"     ->
        text("representativeContactUkAddress.error.town.required")
          .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.town.length")),
      "county"   ->
        optional(
          Forms.text
            .verifying(maxLength(linesMaxLength, "representativeContactUkAddress.error.county.length"))
        ),
      "postCode" ->
        text("representativeContactUkAddress.error.postCode.required")
          .verifying(maxLength(postcodeMaxLength, "representativeContactUkAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
