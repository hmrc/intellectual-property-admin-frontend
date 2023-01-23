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
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

class IpRightsRegistrationNumberFormProvider @Inject() extends Mappings {

  val maxLength: Int                                                                      = 100
  def apply(ipRightsName: String, existingRegistrationNumbers: Seq[String]): Form[String] = {

    val duplicateRegistrationNumberConstraint: Constraint[String] = Constraint { regNum =>
      if (existingRegistrationNumbers contains regNum.toUpperCase) {
        Invalid("ipRightsRegistrationNumber.error.duplicate", regNum)
      } else {
        Valid
      }
    }

    Form(
      "value" -> text("ipRightsRegistrationNumber.error.required", ipRightsName)
        .verifying(maxLength(maxLength, "ipRightsRegistrationNumber.error.length", ipRightsName))
        .verifying(duplicateRegistrationNumberConstraint)
    )
  }
}
