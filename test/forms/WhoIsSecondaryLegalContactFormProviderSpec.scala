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
import models.WhoIsSecondaryLegalContact
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class WhoIsSecondaryLegalContactFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val errorKey                               = "whoIsSecondaryLegalContact.error.required"
  val formProvider                           = new WhoIsSecondaryLegalContactFormProvider()
  val form: Form[WhoIsSecondaryLegalContact] = formProvider(stubMessages)

  val nameLimit: Int   = 200
  val phonesLimit: Int = 100

  val regexKey             = "regex.error"
  val regexKeyNoAmpersand  = "error.regexXSSNoAmpersand"
  val companyNameFieldName = "companyName"
  val companyNameKey       = "whoIsSecondaryLegalContact.companyName.label"
  val nameFieldName        = "name"
  val nameKey              = "whoIsSecondaryLegalContact.name.label"
  val telephoneFieldName   = "telephone"
  val telephoneKey         = "whoIsSecondaryLegalContact.telephone.label"

  "companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "whoIsSecondaryLegalContact.error.companyName.required"
    val lengthKey   = "whoIsSecondaryLegalContact.error.companyName.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "name" must {

    val fieldName   = "name"
    val requiredKey = "whoIsSecondaryLegalContact.error.name.required"
    val lengthKey   = "whoIsSecondaryLegalContact.error.name.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "telephone" must {

    val fieldName   = "telephone"
    val requiredKey = "whoIsSecondaryLegalContact.error.telephone.required"
    val lengthKey   = "whoIsSecondaryLegalContact.error.telephone.length"

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

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "An error" must {
    "be returned when passing an invalid character in one form field" in {
      val testInput        = Map(
        "companyName" -> "company 1<>",
        "name"        -> "name",
        "telephone"   -> "0203234567",
        "email"       -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(companyNameFieldName, regexKeyNoAmpersand, ArraySeq(companyNameKey)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "companyName" -> "company 1<>",
        "name"        -> "name&",
        "telephone"   -> "<&>",
        "email"       -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(companyNameFieldName, regexKeyNoAmpersand, ArraySeq(companyNameKey)),
        FormError(nameFieldName, regexKey, ArraySeq(nameKey)),
        FormError(telephoneFieldName, regexKey, ArraySeq(telephoneKey))
      )
    }
  }
}
