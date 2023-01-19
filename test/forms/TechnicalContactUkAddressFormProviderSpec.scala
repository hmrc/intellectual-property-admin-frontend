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

class TechnicalContactUkAddressFormProviderSpec extends StringFieldBehaviours {

  val linesMaxLength: Int    = 100
  val postcodeMaxLength: Int = 10

  val form = new TechnicalContactUkAddressFormProvider()()

  ".line1" must {

    val fieldName   = "line1"
    val requiredKey = "technicalContactUkAddress.error.line1.required"
    val lengthKey   = "technicalContactUkAddress.error.line1.length"

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
    val lengthKey = "technicalContactUkAddress.error.line2.length"

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
    val requiredKey = "technicalContactUkAddress.error.town.required"
    val lengthKey   = "technicalContactUkAddress.error.town.length"

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
    val lengthKey = "technicalContactUkAddress.error.county.length"

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
    val requiredKey = "technicalContactUkAddress.error.postCode.required"
    val lengthKey   = "technicalContactUkAddress.error.postCode.length"

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
}
