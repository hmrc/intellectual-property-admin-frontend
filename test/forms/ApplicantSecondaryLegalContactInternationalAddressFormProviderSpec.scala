/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ApplicantSecondaryLegalContactInternationalAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new ApplicantSecondaryLegalContactInternationalAddressFormProvider()()
  val maxLength = 100

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "applicantSecondaryLegalContactInternationalAddress.error.line1.required"
    val lengthKey = "applicantSecondaryLegalContactInternationalAddress.error.line1.length"


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
    val lengthKey = "applicantSecondaryLegalContactInternationalAddress.error.line2.length"

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
    val requiredKey = "applicantSecondaryLegalContactInternationalAddress.error.town.required"
    val lengthKey = "applicantSecondaryLegalContactInternationalAddress.error.town.length"

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
    val requiredKey = "applicantSecondaryLegalContactInternationalAddress.error.country.required"
    val lengthKey = "applicantSecondaryLegalContactInternationalAddress.error.country.length"

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
    val lengthKey = "applicantSecondaryLegalContactInternationalAddress.error.postCode.length"

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
