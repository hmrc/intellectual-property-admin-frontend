/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhoIsTechnicalContactFormProviderSpec extends StringFieldBehaviours {

  val form = new WhoIsTechnicalContactFormProvider()()
  val nameLimit: Int = 200
  val phonesLimit: Int = 100

  ".companyName" must {

    val fieldName = "companyName"
    val requiredKey = "whoIsTechnicalContact.error.companyName.required"
    val lengthKey = "whoIsTechnicalContact.error.companyName.length"

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

    val fieldName = "contactName"
    val requiredKey = "whoIsTechnicalContact.error.contactName.required"
    val lengthKey = "whoIsTechnicalContact.error.contactName.length"

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

    val fieldName = "contactTelephone"
    val requiredKey = "whoIsTechnicalContact.error.contactTelephone.required"
    val lengthKey = "whoIsTechnicalContact.error.contactTelephone.length"

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

    val fieldName = "contactEmail"
    val requiredKey = "email.required"
    val lengthKey = "email.length"

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
}
