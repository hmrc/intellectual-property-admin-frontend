/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ApplicantLegalContactFormProviderSpec extends StringFieldBehaviours {

  val form = new ApplicantLegalContactFormProvider()()

  val nameEmailLimit: Int = 200
  val phonesLimit: Int = 100

  "companyName" must {

    val fieldName = "companyName"
    val requiredKey = "applicantLegalContact.error.companyName.required"
    val lengthKey = "applicantLegalContact.error.companyName.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameEmailLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameEmailLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameEmailLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "name" must {

    val fieldName = "name"
    val requiredKey = "applicantLegalContact.error.name.required"
    val lengthKey = "applicantLegalContact.error.name.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameEmailLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = nameEmailLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(nameEmailLimit))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "telephone" must {

    val fieldName = "telephone"
    val requiredKey = "applicantLegalContact.error.telephone.required"
    val lengthKey = "applicantLegalContact.error.telephone.length"


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

  "otherTelephone" must {

    val fieldName = "otherTelephone"
    val lengthKey = "applicantLegalContact.error.otherTelephone.length"

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

    println(FormError(fieldName, requiredKey))

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
