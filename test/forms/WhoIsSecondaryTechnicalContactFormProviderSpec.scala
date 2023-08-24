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
import models.TechnicalContact
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class WhoIsSecondaryTechnicalContactFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val errorKey                     = "whoIsSecondaryTechnicalContact.error.required"
  val formProvider                 = new WhoIsSecondaryTechnicalContactFormProvider()
  val form: Form[TechnicalContact] = formProvider(stubMessages)

  val nameLimit: Int   = 200
  val phonesLimit: Int = 100

  val regexKey             = "regex.error"
  val nameFieldName        = "name"
  val companyNameFieldName = "companyName"
  val telephoneFieldName   = "telephone"

  val nameKey        = "whoIsSecondaryTechnicalContact.name.label"
  val companyNameKey = "whoIsSecondaryTechnicalContact.companyName.label"
  val telephoneKey   = "whoIsSecondaryTechnicalContact.telephone.label"

  "name" must {

    val fieldName   = "name"
    val requiredKey = "whoIsSecondaryTechnicalContact.error.name.required"
    val lengthKey   = "whoIsSecondaryTechnicalContact.error.name.length"

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
    val requiredKey = "whoIsSecondaryTechnicalContact.error.telephone.required"
    val lengthKey   = "whoIsSecondaryTechnicalContact.error.telephone.length"

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
      stringsWithMaxLength(nameLimit)
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
        "name"        -> "&",
        "companyName" -> "Company 1",
        "telephone"   -> "1234",
        "email"       -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(nameFieldName, regexKey, ArraySeq(nameKey)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "name"        -> "&",
        "companyName" -> "Company<>",
        "telephone"   -> "1234>",
        "email"       -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(nameFieldName, regexKey, ArraySeq(nameKey)),
        FormError(companyNameFieldName, regexKey, ArraySeq(companyNameKey)),
        FormError(telephoneFieldName, regexKey, ArraySeq(telephoneKey))
      )
    }
  }
}
