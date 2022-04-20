/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhoIsSecondaryTechnicalContactFormProviderSpec extends StringFieldBehaviours {

  val errorKey = "whoIsSecondaryTechnicalContact.error.required"
  val form = new WhoIsSecondaryTechnicalContactFormProvider()()

  val nameLimit: Int = 200
  val phonesLimit: Int = 100

  "name" must {

    val fieldName = "name"
    val requiredKey = "whoIsSecondaryTechnicalContact.error.name.required"
    val lengthKey = "whoIsSecondaryTechnicalContact.error.name.length"

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
    val requiredKey = "whoIsSecondaryTechnicalContact.error.telephone.required"
    val lengthKey = "whoIsSecondaryTechnicalContact.error.telephone.length"


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
      stringsWithMaxLength(nameLimit)
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
