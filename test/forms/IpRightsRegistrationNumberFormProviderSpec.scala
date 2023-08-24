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

import forms.behaviours.StringFieldBehaviours
import models.IpRightsType
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class IpRightsRegistrationNumberFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val requiredKey          = "ipRightsRegistrationNumber.error.required"
  val lengthKey            = "ipRightsRegistrationNumber.error.length"
  val maxLength            = 100
  val ipRightsType: String = IpRightsType.Copyright.toString
  val regexKey             = "regex.error"
  val valueFieldName       = "value"
  val valueKey             = "ipRightsRegistrationNumber.checkYourAnswersLabel"

  val form = new IpRightsRegistrationNumberFormProvider()(ipRightsType, Seq("firstRegNum", "secondRegNum"))(
    stubMessages
  )

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

      val form = new IpRightsRegistrationNumberFormProvider()(regNum, Seq("REG NUM"))(stubMessages)

      val result = form.bind(Map("value" -> "reg num")).apply("value")
      result.errors shouldEqual Seq(FormError("value", "ipRightsRegistrationNumber.error.duplicate", Seq("reg num")))
    }
  }

  "An error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "value" -> "&&&"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(valueFieldName, regexKey, ArraySeq(valueKey)))
    }
  }
}
