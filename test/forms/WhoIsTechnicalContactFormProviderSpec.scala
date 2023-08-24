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

class WhoIsTechnicalContactFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val formProvider = new WhoIsTechnicalContactFormProvider()

  val form: Form[TechnicalContact] = formProvider(stubMessages)
  val nameLimit: Int               = 200
  val phonesLimit: Int             = 100

  val regexKey                  = "regex.error"
  val contactNameFieldName      = "contactName"
  val contactNameKey            = "whoIsTechnicalContact.contactName"
  val companyNameFieldName      = "companyName"
  val companyNameKey            = "whoIsTechnicalContact.companyName"
  val contactTelephoneFieldName = "contactTelephone"
  val contactTelephoneKey       = "whoIsTechnicalContact.contactTelephone"

  ".companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "whoIsTechnicalContact.error.companyName.required"
    val lengthKey   = "whoIsTechnicalContact.error.companyName.length"

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

  ".contactName" must {

    val fieldName   = "contactName"
    val requiredKey = "whoIsTechnicalContact.error.contactName.required"
    val lengthKey   = "whoIsTechnicalContact.error.contactName.length"

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

  ".contactTelephone" must {

    val fieldName   = "contactTelephone"
    val requiredKey = "whoIsTechnicalContact.error.contactTelephone.required"
    val lengthKey   = "whoIsTechnicalContact.error.contactTelephone.length"

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

  ".contactEmail" must {

    val fieldName   = "contactEmail"
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
        "contactName"      -> "bob",
        "companyName"      -> "company 1&",
        "contactTelephone" -> "0203495678",
        "contactEmail"     -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(companyNameFieldName, regexKey, ArraySeq(companyNameKey)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "contactName"      -> "bob&<>",
        "companyName"      -> "company 1&",
        "contactTelephone" -> "0203495678 &&*",
        "contactEmail"     -> "email@email.com"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(contactNameFieldName, regexKey, ArraySeq(contactNameKey)),
        FormError(companyNameFieldName, regexKey, ArraySeq(companyNameKey)),
        FormError(contactTelephoneFieldName, regexKey, ArraySeq(contactTelephoneKey))
      )
    }
  }
}
