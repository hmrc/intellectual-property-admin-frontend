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

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class IpRightsRegistrationEndFormProviderSpec extends DateBehaviours {

  val rightType = "design"

  val form = new IpRightsRegistrationEndFormProvider()(Seq(rightType))

  ".value" should {

    val validData = datesBetween(
      min = LocalDate.now,
      max = LocalDate.of(2100, 1, 1)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(
      form,
      "value",
      "ipRightsRegistrationEnd.error.required.all",
      errorArgs = Seq(rightType)
    )

    behave like minDateOf(form, LocalDate.now.plusDays(1), "ipRightsRegistrationEnd.error.past", returnsDesign = true)

    behave like dateFieldWithMax(
      form,
      "value",
      max = LocalDate.of(9999, 12, 31),
      FormError("value", "ipRightsRegistrationEnd.error.invalid", List(rightType))
    )
  }
}
