/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

class IpRightsRegistrationNumberFormProvider @Inject() extends Mappings {

  val maxLength:Int = 100
  def apply(ipRightsName: String, existingRegistrationNumbers: Seq[String]): Form[String] = {

    val duplicateRegistrationNumberConstraint: Constraint[String] = Constraint {
      regNum =>
        if (existingRegistrationNumbers contains regNum.toUpperCase) {
          Invalid("ipRightsRegistrationNumber.error.duplicate", regNum)
        } else {
          Valid
        }
    }

    Form(
      "value" -> text("ipRightsRegistrationNumber.error.required", ipRightsName)
        .verifying(maxLength(maxLength, "ipRightsRegistrationNumber.error.length", ipRightsName))
        .verifying(duplicateRegistrationNumberConstraint)
    )
  }
}
