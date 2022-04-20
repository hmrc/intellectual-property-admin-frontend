/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class IpRightsRegistrationEndFormProvider @Inject() extends Mappings {

  val year: Int = 9999
  val month: Int = 12
  val dayOfMonth: Int = 31

  private val maximum = LocalDate.of(year, month, dayOfMonth)

  def apply(args: Seq[String]): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "ipRightsRegistrationEnd.error.invalid",
        allRequiredKey = "ipRightsRegistrationEnd.error.required.all",
        twoRequiredKey = "ipRightsRegistrationEnd.error.required.two",
        requiredKey    = "ipRightsRegistrationEnd.error.required",
        args           = args
      ).verifying(
        minDate(LocalDate.now.plusDays(1), "ipRightsRegistrationEnd.error.past", args: _*),
        maxDate(maximum, "ipRightsRegistrationEnd.error.invalid", args: _*)
      )
    )
}
