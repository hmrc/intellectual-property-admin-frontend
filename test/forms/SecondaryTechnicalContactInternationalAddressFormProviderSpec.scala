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
import models.InternationalAddress
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class SecondaryTechnicalContactInternationalAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val maxLength: Int    = 100
  val regexKey          = "regex.error"
  val townFieldName     = "town"
  val townKey           = "secondaryTechnicalContactInternationalAddress.town"
  val countryFieldName  = "country"
  val countryKey        = "secondaryTechnicalContactInternationalAddress.country"
  val postCodeFieldName = "postCode"
  val postCodeKey       = "secondaryTechnicalContactInternationalAddress.postCode.noOption"

  val formProvider                     = new SecondaryTechnicalContactInternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider(stubMessages)

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "secondaryTechnicalContactInternationalAddress.error.line1.required"
    val lengthKey   = "secondaryTechnicalContactInternationalAddress.error.line1.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".line2" must {

    val fieldName = "line2"
    val lengthKey = "secondaryTechnicalContactInternationalAddress.error.line2.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName
    )
  }

  ".town" must {

    val fieldName   = "town"
    val requiredKey = "secondaryTechnicalContactInternationalAddress.error.town.required"
    val lengthKey   = "secondaryTechnicalContactInternationalAddress.error.town.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".country" must {

    val fieldName   = "country"
    val requiredKey = "secondaryTechnicalContactInternationalAddress.error.country.required"
    val lengthKey   = "secondaryTechnicalContactInternationalAddress.error.country.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".postCode" must {

    val fieldName = "postCode"
    val lengthKey = "secondaryTechnicalContactInternationalAddress.error.postCode.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName
    )
  }

  "an error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "line1"    -> "line 1",
        "line2"    -> "line 2",
        "town"     -> "town",
        "country"  -> "country",
        "postCode" -> "SE1 &&&"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(postCodeFieldName, regexKey, ArraySeq(postCodeKey)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "line1"    -> "line 1",
        "line2"    -> "line 2",
        "town"     -> "town<",
        "country"  -> "country<>",
        "postCode" -> "SE1 &&&"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(townFieldName, regexKey, ArraySeq(townKey)),
        FormError(countryFieldName, regexKey, ArraySeq(countryKey)),
        FormError(postCodeFieldName, regexKey, ArraySeq(postCodeKey))
      )
    }
  }
}
