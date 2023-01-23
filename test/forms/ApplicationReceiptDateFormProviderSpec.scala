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

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ApplicationReceiptDateFormProviderSpec extends DateBehaviours {

  val form            = new ApplicationReceiptDateFormProvider()()
  val year: Int       = 2018
  val month: Int      = 1
  val dayOfMonth: Int = 1

  ".value" should {

    val validData = datesBetween(
      min = LocalDate.of(year, month, dayOfMonth),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "applicationReceiptDate.error.required.all")

    behave like dateFieldWithMax(
      form,
      "value",
      max = LocalDate.now,
      FormError("value", "applicationReceiptDate.error.future", List("day", "month", "year"))
    )

    behave like minDateOf(
      form,
      LocalDate.of(2018, 1, 1),
      "applicationReceiptDate.error.minDate"
    ) // scalastyle:off magic.number
  }
}
