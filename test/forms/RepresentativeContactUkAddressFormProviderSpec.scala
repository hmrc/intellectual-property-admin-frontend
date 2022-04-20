/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RepresentativeContactUkAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new RepresentativeContactUkAddressFormProvider()()

  val linesMaxLength: Int = 100
  val postcodeMaxLength: Int = 10

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "representativeContactUkAddress.error.line1.required"
    val lengthKey = "representativeContactUkAddress.error.line1.length"

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
    val lengthKey = "representativeContactUkAddress.error.line2.length"

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

    val fieldName = "town"
    val requiredKey = "representativeContactUkAddress.error.town.required"
    val lengthKey = "representativeContactUkAddress.error.town.length"

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
    val lengthKey = "representativeContactUkAddress.error.county.length"

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

    val fieldName = "postCode"
    val requiredKey = "representativeContactUkAddress.error.postCode.required"
    val lengthKey = "representativeContactUkAddress.error.postCode.length"

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
