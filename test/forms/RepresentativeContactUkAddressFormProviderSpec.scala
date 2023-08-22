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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class RepresentativeContactUkAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val formProvider = new RepresentativeContactUkAddressFormProvider()
  val form         = formProvider(stubMessages)

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  val regexKey       = "regex.error"
  val line1FieldName = "line1"
  val line1Key       = "representativeContactUkAddress.line1"
  val line2FieldName = "line2"
  val line2Key       = "representativeContactUkAddress.line2"
  val townFieldName  = "town"
  val townKey        = "representativeContactUkAddress.town"

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "representativeContactUkAddress.error.line1.required"
    val lengthKey   = "representativeContactUkAddress.error.line1.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(linesMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = linesMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(linesMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".line2" must {

    val fieldName = "line2"
    val lengthKey = "representativeContactUkAddress.error.line2.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(linesMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = linesMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(linesMaxLength))
    )

    behave like optionalField(
      form,
      fieldName
    )
  }

  ".town" must {

    val fieldName   = "town"
    val requiredKey = "representativeContactUkAddress.error.town.required"
    val lengthKey   = "representativeContactUkAddress.error.town.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(linesMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = linesMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(linesMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".county" must {

    val fieldName = "county"
    val lengthKey = "representativeContactUkAddress.error.county.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(linesMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = linesMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(linesMaxLength))
    )

    behave like optionalField(
      form,
      fieldName
    )
  }

  ".postCode" must {

    val fieldName   = "postCode"
    val requiredKey = "representativeContactUkAddress.error.postCode.required"
    val lengthKey   = "representativeContactUkAddress.error.postCode.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(postcodeMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = postcodeMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(postcodeMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "an error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "line1"    -> "address 1",
        "line2"    -> "address 2&&",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "nw1 2as"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(line2FieldName, regexKey, ArraySeq(line2Key)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "line1"    -> "address 1<>",
        "line2"    -> "address 2&&",
        "town"     -> "<&>",
        "county"   -> "county",
        "postCode" -> "nw1 2as"
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
