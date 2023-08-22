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

class CompanyApplyingUkAddressFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10
  val regexErrorKey: String  = "regex.error"

  val formProvider          = new CompanyApplyingUkAddressFormProvider()
  val form: Form[UkAddress] = formProvider(stubMessages)

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "companyApplyingUkAddress.error.line1.required"
    val lengthKey   = "companyApplyingUkAddress.error.line1.length"

    val line1Key = "companyApplyingUkAddress.line1"

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
        "line1"    -> "<>",
        "line2"    -> "address",
        "town"     -> "Camden",
        "county"   -> "Greater London",
        "postCode" -> "N19 3QA"
      )

      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexErrorKey, ArraySeq(line1Key)))
    }
  }

  ".line2" must {

    val fieldName = "line2"
    val lengthKey = "companyApplyingUkAddress.error.line2.length"

    val line2Key = "companyApplyingUkAddress.line2"

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
        "line1"    -> "Address 1",
        "line2"    -> "&",
        "town"     -> "Camden",
        "county"   -> "Greater London",
        "postCode" -> "N19 3QA"
      )

      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexErrorKey, ArraySeq(line2Key)))
    }
  }

  ".town" must {

    val fieldName   = "town"
    val requiredKey = "companyApplyingUkAddress.error.town.required"
    val lengthKey   = "companyApplyingUkAddress.error.town.length"
    val townKey     = "companyApplyingUkAddress.town"

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
      val testInput        = Map(
        "line1"    -> "Address 1",
        "line2"    -> "Address 2",
        "town"     -> ">",
        "county"   -> "Greater London",
        "postCode" -> "N19 3QA"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexErrorKey, ArraySeq(townKey)))
    }
  }

  ".county" must {

    val fieldName = "county"
    val lengthKey = "companyApplyingUkAddress.error.county.length"
    val countyKey = "companyApplyingUkAddress.county"

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
      val testInput        = Map(
        "line1"    -> "Address 1",
        "line2"    -> "Address 2",
        "town"     -> "Town",
        "county"   -> "<Greater London>",
        "postCode" -> "N19 3QA"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexErrorKey, ArraySeq(countyKey)))
    }
  }

  ".postCode" must {

    val fieldName   = "postCode"
    val requiredKey = "companyApplyingUkAddress.error.postCode.required"
    val lengthKey   = "companyApplyingUkAddress.error.postCode.length"
    val postCodeKey = "companyApplyingUkAddress.postCode"

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
      val testInput        = Map(
        "line1"    -> "Address 1",
        "line2"    -> "Address 2",
        "town"     -> "Town",
        "county"   -> "Greater London",
        "postCode" -> "&&&"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexErrorKey, ArraySeq(postCodeKey)))
    }
  }
}
