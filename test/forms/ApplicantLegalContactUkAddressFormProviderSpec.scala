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

class ApplicantLegalContactUkAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  val regexKey = "regex.error"

  val formProvider          = new ApplicantLegalContactUkAddressFormProvider()
  val form: Form[UkAddress] = formProvider(stubMessages)

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "applicantLegalContactUkAddress.error.line1.required"
    val lengthKey   = "applicantLegalContactUkAddress.error.line1.length"

    val line1Key = "applicantLegalContactUkAddress.line1"

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

    "return an error when passed a value with an invalid character" in {

      val testInput = Map(
        "line1"    -> "<",
        "line2"    -> "address2",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "abc 123"
      )

      val invalidTestInput = form.bind(testInput).errors

      invalidTestInput shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(line1Key)))
    }
  }

  ".line2" must {

    val fieldName = "line2"
    val lengthKey = "applicantLegalContactUkAddress.error.line2.length"

    val line2Key = "applicantLegalContactUkAddress.line2"

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

    "return an error when passed a value with an invalid character" in {

      val testInput = Map(
        "line1"    -> "address 1",
        "line2"    -> "<>",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "abc 123"
      )

      val invalidTestInput = form.bind(testInput).errors

      invalidTestInput shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(line2Key)))
    }
  }

  ".town" must {

    val fieldName   = "town"
    val requiredKey = "applicantLegalContactUkAddress.error.town.required"
    val lengthKey   = "applicantLegalContactUkAddress.error.town.length"

    val townKey = "applicantLegalContactUkAddress.town"

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

    "return an error when passed a value with an invalid character" in {

      val testInput = Map(
        "line1"    -> "address 1",
        "line2"    -> "address 2",
        "town"     -> "&",
        "county"   -> "county",
        "postCode" -> "abc 123"
      )

      val invalidTestInput = form.bind(testInput).errors

      invalidTestInput shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(townKey)))
    }
  }

  ".county" must {

    val fieldName = "county"
    val lengthKey = "applicantLegalContactUkAddress.error.county.length"

    val countyKey = "applicantLegalContactUkAddress.county.noOption"

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

    "return an error when passed a value with an invalid character" in {

      val testInput = Map(
        "line1"    -> "address 1",
        "line2"    -> "address 2",
        "town"     -> "town",
        "county"   -> "&&",
        "postCode" -> "abc 123"
      )

      val invalidTestInput = form.bind(testInput).errors

      invalidTestInput shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(countyKey)))
    }
  }

  ".postCode" must {

    val fieldName   = "postCode"
    val requiredKey = "applicantLegalContactUkAddress.error.postCode.required"
    val lengthKey   = "applicantLegalContactUkAddress.error.postCode.length"

    val postCodeKey = "applicantLegalContactUkAddress.postCode"

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

    "return an error when passed a value with an invalid character" in {

      val testInput = Map(
        "line1"    -> "address 1",
        "line2"    -> "address 2",
        "town"     -> "town",
        "county"   -> "county",
        "postCode" -> "<>"
      )

      val invalidTestInput = form.bind(testInput).errors

      invalidTestInput shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(postCodeKey)))
    }
  }
}
