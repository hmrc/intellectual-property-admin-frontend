/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ApplicationReceiptDateFormProviderSpec extends DateBehaviours {

  val form = new ApplicationReceiptDateFormProvider()()
  val year: Int = 2018
  val month: Int = 1
  val dayOfMonth: Int = 1

  ".value" should {

    val validData = datesBetween(
      min = LocalDate.of(year, month, dayOfMonth),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "applicationReceiptDate.error.required.all")

    behave like dateFieldWithMax(form, "value",
      max = LocalDate.now,
      FormError("value", "applicationReceiptDate.error.future", List("day", "month", "year"))
    )

    behave like minDateOf(form, LocalDate.of(2018, 1, 1), "applicationReceiptDate.error.minDate") //scalastyle:off magic.number
  }
}
