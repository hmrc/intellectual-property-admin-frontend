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
import models.UkAddress
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class ApplicantSecondaryLegalContactUkAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  val regexKey          = "regex.error"
  val line1FieldName    = "line1"
  val line2FieldName    = "line2"
  val postCodeFieldName = "postCode"

  val line1Key    = "applicantSecondaryLegalContactUkAddress.line1"
  val line2Key    = "applicantSecondaryLegalContactUkAddress.line2"
  val postCodeKey = "applicantSecondaryLegalContactUkAddress.postCode"

  val formProvider          = new ApplicantSecondaryLegalContactUkAddressFormProvider()
  val form: Form[UkAddress] = formProvider(stubMessages)

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "applicantSecondaryLegalContactUkAddress.error.line1.required"
    val lengthKey   = "applicantSecondaryLegalContactUkAddress.error.line1.length"

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
    val lengthKey = "applicantSecondaryLegalContactUkAddress.error.line2.length"

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
    val requiredKey = "applicantSecondaryLegalContactUkAddress.error.town.required"
    val lengthKey   = "applicantSecondaryLegalContactUkAddress.error.town.length"

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
    val lengthKey = "applicantSecondaryLegalContactUkAddress.error.county.length"

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
    val requiredKey = "applicantSecondaryLegalContactUkAddress.error.postCode.required"
    val lengthKey   = "applicantSecondaryLegalContactUkAddress.error.postCode.length"

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

  "An error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "line1"    -> "address&",
        "line2"    -> "address 2",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "postCode"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(line1FieldName, regexKey, ArraySeq(line1Key)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "line1"    -> "address&",
        "line2"    -> "address 2<",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "<>"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(line1FieldName, regexKey, ArraySeq(line1Key)),
        FormError(line2FieldName, regexKey, ArraySeq(line2Key)),
        FormError(postCodeFieldName, regexKey, ArraySeq(postCodeKey))
      )
    }
  }
}
