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
import models.RepresentativeDetails
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class RepresentativeContactFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val nameLimit: Int      = 200
  val phoneRoleLimit: Int = 100

  val regexKey            = "regex.error"
  val regexKeyNoAmpersand = "error.regexXSSNoAmpersand"
  val nameKey             = "representativeContact.name.label"
  val companyNameKey      = "representativeContact.companyName.label"
  val roleKey             = "representativeContact.role.label.noOption"
  val nameFieldName       = "name"
  val companyFieldName    = "companyName"
  val roleFieldName       = "role"

  val formProvider                      = new RepresentativeContactFormProvider()
  val form: Form[RepresentativeDetails] = formProvider(stubMessages)

  "companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "representativeContact.error.companyName.required"
    val lengthKey   = "representativeContact.error.companyName.length"

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
    val requiredKey = "representativeContact.error.name.required"
    val lengthKey   = "representativeContact.error.name.length"

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

  "role" must {

    val fieldName = "role"
    val lengthKey = "representativeContact.error.role.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phoneRoleLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phoneRoleLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phoneRoleLimit))
    )
  }

  "telephone" must {

    val fieldName = "telephone"
    val lengthKey = "representativeContact.error.telephone.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phoneRoleLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phoneRoleLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phoneRoleLimit))
    )
  }

  "email" must {

    val fieldName   = "email"
    val requiredKey = "email.required"
    val lengthKey   = "email.length"

    val emailLimit = 256

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = emailLimit,
      lengthError = FormError(fieldName, lengthKey)
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
        "name"        -> "name&&",
        "companyName" -> "Microsoft",
        "telephone"   -> "01273 123 123",
        "email"       -> "email@email.com",
        "role"        -> "role"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(nameFieldName, regexKey, ArraySeq(nameKey)))
    }

    "be returned when passing an invalid character in multiple form fields" in {
      val testInput        = Map(
        "name"        -> "name&&",
        "companyName" -> "Microsoft<>",
        "telephone"   -> "01273 123 123",
        "email"       -> "email@email.com",
        "role"        -> "role&<>"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(
        FormError(nameFieldName, regexKey, ArraySeq(nameKey)),
        FormError(companyFieldName, regexKeyNoAmpersand, ArraySeq(companyNameKey)),
        FormError(roleFieldName, regexKey, ArraySeq(roleKey))
      )
    }
  }
}
