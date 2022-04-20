/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class IpRightsDescriptionWithBrandFormProviderSpec extends StringFieldBehaviours {

  val form = new IpRightsDescriptionWithBrandFormProvider()()
  val brandMaxLength: Int = 100
  val descriptionMaxLength: Int = 1000
  ".brand" must {

    val fieldName = "brand"
    val requiredKey = "ipRightsDescriptionWithBrand.error.brand.required"
    val lengthKey = "ipRightsDescriptionWithBrand.error.brand.length"

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

    val fieldName = "value"
    val requiredKey = "ipRightsDescriptionWithBrand.error.description.required"
    val lengthKey = "ipRightsDescriptionWithBrand.error.description.length"

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
