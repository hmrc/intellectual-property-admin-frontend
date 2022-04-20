/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.NiceClassId
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

class IpRightsNiceClassFormProvider @Inject() extends Mappings {

  def apply(existingNiceClasses: Seq[NiceClassId]): Form[NiceClassId] = {

    val duplicateNiceClassConstraint: Constraint[NiceClassId] = Constraint {
      id =>
        if (existingNiceClasses contains id) {
          Invalid("ipRightsNiceClass.error.duplicate", id)
        } else {
          Valid
        }
    }

    Form(
      "value" -> text("ipRightsNiceClass.error.required")
        .verifying("ipRightsNiceClass.error.format", NiceClassId.fromString(_).isDefined)
        .transform(NiceClassId.fromString(_).get, (_: NiceClassId).toString)
        .verifying(duplicateNiceClassConstraint)
    )
  }
}
