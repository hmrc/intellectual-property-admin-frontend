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
import models.CompanyApplying
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale
import scala.collection.immutable.ArraySeq

class CompanyApplyingFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val maxLength           = 200
  val regexKey            = "regex.error"
  val regexKeyNoAmpersand = "error.regexXSSNoAmpersand"

  val formProvider                = new CompanyApplyingFormProvider()
  val form: Form[CompanyApplying] = formProvider(stubMessages)

  ".companyName" must {

    val fieldName   = "companyName"
    val requiredKey = "companyApplying.error.companyName.required"
    val lengthKey   = "companyApplying.error.companyName.length"

    val companyNameKey = "companyApplying.companyName"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "return an error when passed a value with an invalid character" in {

      val testInput        = Map(
        "companyName"    -> "<",
        "companyAcronym" -> "ABC"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexKeyNoAmpersand, ArraySeq(companyNameKey)))
    }
  }

  ".companyAcronym" must {

    val fieldName = "companyAcronym"
    val lengthKey = "companyApplying.error.companyAcronym.length"

    val companyAcronymKey = "companyApplying.companyAcronym.noOption"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName
    )

    "return an error when passed a value with an invalid character" in {

      val testInput        = Map(
        "companyName"    -> "company",
        "companyAcronym" -> "&>"
      )
      val invalidValueTest = form.bind(testInput).errors

      invalidValueTest shouldBe Seq(FormError(fieldName, regexKey, ArraySeq(companyAcronymKey)))
    }

  }
}
