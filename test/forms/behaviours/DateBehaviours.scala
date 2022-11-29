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

package forms.behaviours

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

class DateBehaviours extends FieldBehaviours {

  def dateField(form: Form[_], key: String, validData: Gen[LocalDate]): Unit =
    "bind valid data" in {

      forAll(validData -> "valid date") { date =>
        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthValue.toString,
          s"$key.year"  -> date.getYear.toString
        )

        val result = form.bind(data)

        result.value.value shouldEqual date
      }
    }

  def dateFieldWithMax(form: Form[_], key: String, max: LocalDate, formError: FormError): Unit =
    s"fail to bind a date greater than ${max.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = datesBetween(max.plusDays(1), max.plusYears(10))

      forAll(generator -> "invalid dates") { date =>
        val data = Map(
          s"$key.day"   -> date.getDayOfMonth.toString,
          s"$key.month" -> date.getMonthValue.toString,
          s"$key.year"  -> date.getYear.toString
        )

        val result = form.bind(data)

        result.errors should contain only formError
      }
    }

  def minDateOf(
    form: Form[_],
    minDate: LocalDate,
    errorKey: String,
    returnsDesign: Boolean = false,
    returnsArray: Boolean = false
  ): Unit = {

    val minDatePlusOne: LocalDate  = minDate.plusDays(1)
    val minDateMinusOne: LocalDate = minDate.minusDays(1)

    "return no error if on the minimum date" in {
      val result = form.bind(
        Map(
          "value.day"   -> minDate.getDayOfMonth.toString,
          "value.month" -> minDate.getMonthValue.toString,
          "value.year"  -> minDate.getYear.toString
        )
      )

      result.value.value shouldEqual minDate
    }

    "not return an error if past the minimum date by one" in {
      val result = form.bind(
        Map(
          "value.day"   -> minDatePlusOne.getDayOfMonth.toString,
          "value.month" -> minDatePlusOne.getMonthValue.toString,
          "value.year"  -> minDatePlusOne.getYear.toString
        )
      )

      result.value.value shouldEqual minDatePlusOne
    }

    "return an error if before the minimum date by one" in {
      val result = form
        .bind(
          Map(
            "value.day"   -> minDateMinusOne.getDayOfMonth.toString,
            "value.month" -> minDateMinusOne.getMonthValue.toString,
            "value.year"  -> minDateMinusOne.getYear.toString
          )
        )
        .apply("value")

      if (returnsDesign) {
        result.errors shouldEqual Seq(FormError("value", errorKey, List("design")))
      } else if (returnsArray) {
        result.errors shouldEqual Seq(FormError("value", errorKey, List()))
      } else {
        result.errors shouldEqual Seq(FormError("value", errorKey, List("day", "month", "year")))
      }
    }
  }

  def mandatoryDateField(form: Form[_], key: String, requiredAllKey: String, errorArgs: Seq[String] = Seq.empty): Unit =
    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors should contain only FormError(key, requiredAllKey, errorArgs)
    }
}
