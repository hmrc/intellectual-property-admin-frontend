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
import models.ApplicantLegalContact
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class ApplicantLegalContactFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val formProvider                      = new ApplicantLegalContactFormProvider()
  val form: Form[ApplicantLegalContact] = formProvider(stubMessages)

  val nameEmailLimit: Int = 200
  val phonesLimit: Int    = 100
  val regexKey            = "regex.error"
  val regexKeyNoAmpersand = "error.regexXSSNoAmpersand"

  "companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "applicantLegalContact.error.companyName.required"
    val lengthKey   = "applicantLegalContact.error.companyName.length"

    val companyNameKey = "applicantLegalContact.companyName.label"

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
        "telephone"      -> "+1123",
        "otherTelephone" -> "+1234",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexKeyNoAmpersand, ArraySeq(companyNameKey)))
    }

  }

  "name" must {

    val fieldName   = "name"
    val requiredKey = "applicantLegalContact.error.name.required"
    val lengthKey   = "applicantLegalContact.error.name.length"

    val nameKey = "applicantLegalContact.name.label"

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
        "telephone"      -> "+1123",
        "otherTelephone" -> "07700900982",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(nameKey)))
    }
  }

  "telephone" must {

    val fieldName   = "telephone"
    val requiredKey = "applicantLegalContact.error.telephone.required"

    val telephoneKey            = "applicantLegalContact.telephone.label"
    val telephoneKeyValidFormat = "representativeContact.error.telephone.validFormat"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phonesLimit)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "return an error when passed a value with an invalid character" in {
      val testInput        = Map(
        "companyName" -> "company",
        "name"        -> "name",
        "telephone"   -> "07462310993<",
        "email"       -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(fieldName, telephoneKeyValidFormat),
        FormError(fieldName, regexKey, ArraySeq(telephoneKey))
      )
    }

    "return an error when an invalid number has been entered" in {
      val testInput        = Map(
        "name"        -> "name",
        "companyName" -> "Microsoft",
        "telephone"   -> "abc",
        "email"       -> "email@email.com",
        "role"        -> "role"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, telephoneKeyValidFormat))
    }
  }

  "otherTelephone" must {

    val fieldName                    = "otherTelephone"
    val otherTelephoneKey            = "applicantLegalContact.otherTelephone.label.noOption"
    val otherTelephoneKeyValidFormat = "representativeContact.error.telephone.validFormat"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phonesLimit)
    )

    "return an error when passed a value with an invalid number" in {
      val testInput        = Map(
        "companyName"    -> "company",
        "name"           -> "name",
        "telephone"      -> "+1123",
        "otherTelephone" -> "1234<>",
        "email"          -> "email@email"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(fieldName, otherTelephoneKeyValidFormat),
        FormError(fieldName, regexKey, ArraySeq(otherTelephoneKey))
      )
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
