/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhoIsSecondaryLegalContactFormProviderSpec extends StringFieldBehaviours {

  val errorKey = "whoIsSecondaryLegalContact.error.required"
  val form = new WhoIsSecondaryLegalContactFormProvider()()

  val nameLimit: Int = 200
  val phonesLimit: Int = 100

  "companyName" must {

    val fieldName = "companyName"
    val requiredKey = "whoIsSecondaryLegalContact.error.companyName.required"
    val lengthKey = "whoIsSecondaryLegalContact.error.companyName.length"

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

    val fieldName = "name"
    val requiredKey = "whoIsSecondaryLegalContact.error.name.required"
    val lengthKey = "whoIsSecondaryLegalContact.error.name.length"

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

  "telephone" must {

    val fieldName = "telephone"
    val requiredKey = "whoIsSecondaryLegalContact.error.telephone.required"
    val lengthKey = "whoIsSecondaryLegalContact.error.telephone.length"


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

  "email" must {

    val fieldName = "email"
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
