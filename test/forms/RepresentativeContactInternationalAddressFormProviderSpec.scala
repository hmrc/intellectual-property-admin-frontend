/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RepresentativeContactInternationalAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new RepresentativeContactInternationalAddressFormProvider()()
  val maxLength = 100

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "representativeContactInternationalAddress.error.line1.required"
    val lengthKey = "representativeContactInternationalAddress.error.line1.length"

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
  }

  ".line2" must {

    val fieldName = "line2"
    val lengthKey = "representativeContactInternationalAddress.error.line2.length"

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
  }

  ".town" must {

    val fieldName = "town"
    val requiredKey = "representativeContactInternationalAddress.error.town.required"
    val lengthKey = "representativeContactInternationalAddress.error.town.length"

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
  }

  ".country" must {

    val fieldName = "country"
    val requiredKey = "representativeContactInternationalAddress.error.country.required"
    val lengthKey = "representativeContactInternationalAddress.error.country.length"

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
  }

  ".postCode" must {

    val fieldName = "postCode"
    val lengthKey = "representativeContactInternationalAddress.error.postCode.length"

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
  }
}
