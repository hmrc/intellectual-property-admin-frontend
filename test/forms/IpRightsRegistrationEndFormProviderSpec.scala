/*
 * Copyright 2022 HM Revenue & Customs
 *
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

    behave like mandatoryDateField(form, "value", "ipRightsRegistrationEnd.error.required.all", errorArgs = Seq(rightType))

    behave like minDateOf(form,
      LocalDate.now.plusDays(1),
      "ipRightsRegistrationEnd.error.past",
      returnsDesign = true
    )

    behave like dateFieldWithMax(form, "value",
      max = LocalDate.of(9999, 12, 31),
      FormError("value", "ipRightsRegistrationEnd.error.invalid", List(rightType))
    )
  }
}
