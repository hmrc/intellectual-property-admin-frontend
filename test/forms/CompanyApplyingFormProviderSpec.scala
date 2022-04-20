/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CompanyApplyingFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 200

  val form = new CompanyApplyingFormProvider()()

  ".companyName" must {

    val fieldName = "companyName"
    val requiredKey = "companyApplying.error.companyName.required"
    val lengthKey = "companyApplying.error.companyName.length"

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

  ".companyAcronym" must {

    val fieldName = "companyAcronym"
    val lengthKey = "companyApplying.error.companyAcronym.length"

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
