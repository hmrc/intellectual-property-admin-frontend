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

class TechnicalContactInternationalAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val maxLength: Int = 100

  val formProvider                     = new TechnicalContactInternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider(stubMessages)

  val regexKey       = "regex.error"
  val line1FieldName = "line1"
  val line2FieldName = "line2"
  val townFieldName  = "town"

  val line1Key = "secondaryTechnicalContactInternationalAddress.line1"
  val line2Key = "secondaryTechnicalContactInternationalAddress.line2"
  val townKey  = "secondaryTechnicalContactInternationalAddress.town"

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "technicalContactInternationalAddress.error.line1.required"
    val lengthKey   = "technicalContactInternationalAddress.error.line1.length"

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
    val lengthKey = "technicalContactInternationalAddress.error.line2.length"

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
    val requiredKey = "technicalContactInternationalAddress.error.town.required"
    val lengthKey   = "technicalContactInternationalAddress.error.town.length"

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
    val requiredKey = "technicalContactInternationalAddress.error.country.required"
    val lengthKey   = "technicalContactInternationalAddress.error.country.length"

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
    val lengthKey = "technicalContactInternationalAddress.error.postCode.length"

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

  "An error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "line1"    -> "address&",
        "line2"    -> "address 2",
        "town"     -> "town",
        "country"  -> "country",
        "postCode" -> "postCode"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(line1FieldName, regexKey, ArraySeq(line1Key)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "line1"    -> "address&",
        "line2"    -> "address 2<",
        "town"     -> "town<&?>",
        "country"  -> "country",
        "postCode" -> "postCode"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(line1FieldName, regexKey, ArraySeq(line1Key)),
        FormError(line2FieldName, regexKey, ArraySeq(line2Key)),
        FormError(townFieldName, regexKey, ArraySeq(townKey))
      )
    }
  }
}
