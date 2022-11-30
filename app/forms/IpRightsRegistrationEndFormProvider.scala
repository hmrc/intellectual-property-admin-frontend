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

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class IpRightsRegistrationEndFormProvider @Inject() extends Mappings {

  val year: Int       = 9999
  val month: Int      = 12
  val dayOfMonth: Int = 31

  private val maximum = LocalDate.of(year, month, dayOfMonth)

  def apply(args: Seq[String]): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "ipRightsRegistrationEnd.error.invalid",
        allRequiredKey = "ipRightsRegistrationEnd.error.required.all",
        twoRequiredKey = "ipRightsRegistrationEnd.error.required.two",
        requiredKey = "ipRightsRegistrationEnd.error.required",
        args = args
      ).verifying(
        minDate(LocalDate.now.plusDays(1), "ipRightsRegistrationEnd.error.past", args: _*),
        maxDate(maximum, "ipRightsRegistrationEnd.error.invalid", args: _*)
      )
    )
}
