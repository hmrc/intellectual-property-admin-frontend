/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import models.IpRightsType
import play.api.data.FormError

class IpRightsRegistrationNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "ipRightsRegistrationNumber.error.required"
  val lengthKey = "ipRightsRegistrationNumber.error.length"
  val maxLength = 100
  val ipRightsType: String = IpRightsType.Copyright.toString

  val form = new IpRightsRegistrationNumberFormProvider()(ipRightsType, Seq("firstRegNum", "secondRegNum"))

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength, ipRightsType))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(ipRightsType))
    )

    "not bind duplicate entries" in {

      val regNum = "registrationNumber"

      val form = new IpRightsRegistrationNumberFormProvider()(regNum, Seq("REG NUM"))

      val result = form.bind(Map("value" -> "reg num")).apply("value")
      result.errors shouldEqual Seq(FormError("value", "ipRightsRegistrationNumber.error.duplicate", Seq("reg num")))
    }
  }
}
