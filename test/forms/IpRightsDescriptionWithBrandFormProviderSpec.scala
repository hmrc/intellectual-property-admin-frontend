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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.IpRightsDescriptionWithBrand
import play.api.data.{Form, FormError}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessagesApi

import java.util.Locale

class IpRightsDescriptionWithBrandFormProviderSpec extends StringFieldBehaviours {

  val stubMessages: Messages = stubMessagesApi().preferred(Seq(Lang(Locale.ENGLISH)))

  val formProvider                             = new IpRightsDescriptionWithBrandFormProvider()
  val form: Form[IpRightsDescriptionWithBrand] = formProvider(stubMessages)
  val brandMaxLength: Int                      = 100
  val descriptionMaxLength: Int                = 1000
  ".brand" must {

    val fieldName   = "brand"
    val requiredKey = "ipRightsDescriptionWithBrand.error.brand.required"
    val lengthKey   = "ipRightsDescriptionWithBrand.error.brand.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(brandMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = brandMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(brandMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".value" must {

    val fieldName   = "value"
    val requiredKey = "ipRightsDescriptionWithBrand.error.description.required"
    val lengthKey   = "ipRightsDescriptionWithBrand.error.description.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(descriptionMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = descriptionMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(descriptionMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
