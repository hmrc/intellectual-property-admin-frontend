/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class ApplicationReceiptDateFormProvider @Inject() extends Mappings {

  val minDate: LocalDate = LocalDate.of(2018,1,1) //scalastyle:off magic.number

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "applicationReceiptDate.error.invalid",
        allRequiredKey = "applicationReceiptDate.error.required.all",
        twoRequiredKey = "applicationReceiptDate.error.required.two",
        requiredKey    = "applicationReceiptDate.error.required"
      ).verifying(maxDate(LocalDate.now, "applicationReceiptDate.error.future", "day", "month", "year"))
        .verifying(minDate(minDate,"applicationReceiptDate.error.minDate", "day", "month", "year"))
    )
}
