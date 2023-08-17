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
import play.api.data.FormError

class ApplicantLegalContactFormProviderSpec extends StringFieldBehaviours {

  val form = new ApplicantLegalContactFormProvider()()

  val nameEmailLimit: Int    = 200
  val phonesLimit: Int       = 100
  val rejectXssChars: String = """^[^<>"&]*$"""
  val regexKey               = "regex.error"

  "companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "applicantLegalContact.error.companyName.required"
    val lengthKey   = "applicantLegalContact.error.companyName.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameEmailLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameEmailLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameEmailLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "return an error when passed a value with an invalid character" in {
      val testInput        = Map(
        "companyName"    -> "<>",
        "name"           -> "name",
        "telephone"      -> "1123",
        "otherTelephone" -> "1234",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest should contain(FormError(fieldName, regexKey, List(rejectXssChars)))
    }

  }

  "name" must {

    val fieldName   = "name"
    val requiredKey = "applicantLegalContact.error.name.required"
    val lengthKey   = "applicantLegalContact.error.name.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameEmailLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameEmailLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameEmailLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "return an error when passed a value with an invalid character" in {
      val testInput        = Map(
        "companyName"    -> "company",
        "name"           -> "<?>",
        "telephone"      -> "1123",
        "otherTelephone" -> "1234",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest should contain(FormError(fieldName, regexKey, List(rejectXssChars)))
    }
  }

  "telephone" must {

    val fieldName   = "telephone"
    val requiredKey = "applicantLegalContact.error.telephone.required"
    val lengthKey   = "applicantLegalContact.error.telephone.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phonesLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phonesLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phonesLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "return an error when passed a value with an invalid character" in {
      val testInput        = Map(
        "companyName"    -> "company",
        "name"           -> "name",
        "telephone"      -> "1123<",
        "otherTelephone" -> "1234",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest should contain(FormError(fieldName, regexKey, List(rejectXssChars)))
    }
  }

  "otherTelephone" must {

    val fieldName = "otherTelephone"
    val lengthKey = "applicantLegalContact.error.otherTelephone.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phonesLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phonesLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phonesLimit))
    )

    "return an error when passed a value with an invalid character" in {
      val testInput        = Map(
        "companyName"    -> "company",
        "name"           -> "name",
        "telephone"      -> "1123",
        "otherTelephone" -> "1234<>",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest should contain(FormError(fieldName, regexKey, List(rejectXssChars)))
    }
  }

  "email" must {

    val fieldName   = "email"
    val requiredKey = "email.required"
    val lengthKey   = "email.length"

    val emailLength = 256

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      emailGenerator
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = emailLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    println(FormError(fieldName, requiredKey))

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
