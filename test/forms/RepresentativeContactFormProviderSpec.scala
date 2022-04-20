/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RepresentativeContactFormProviderSpec extends StringFieldBehaviours {

  val nameLimit: Int = 200
  val phoneRoleLimit: Int = 100

  val form = new RepresentativeContactFormProvider()()

  "companyName" must {

    val fieldName = "companyName"
    val requiredKey = "representativeContact.error.companyName.required"
    val lengthKey = "representativeContact.error.companyName.length"

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
    val requiredKey = "representativeContact.error.name.required"
    val lengthKey = "representativeContact.error.name.length"

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

  "role" must {

    val fieldName = "role"
    val lengthKey = "representativeContact.error.role.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phoneRoleLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phoneRoleLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phoneRoleLimit))
    )
  }

  "telephone" must {

    val fieldName = "telephone"
    val lengthKey = "representativeContact.error.telephone.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(phoneRoleLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = phoneRoleLimit,
      lengthError = FormError(fieldName, lengthKey, Seq(phoneRoleLimit))
    )
  }

  "email" must {

    val fieldName = "email"
    val requiredKey = "email.required"
    val lengthKey = "email.length"

    val emailLimit = 256

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(nameLimit)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = emailLimit,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
