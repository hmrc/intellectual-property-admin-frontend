/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class IsRepresentativeContactUkBasedFormProvider @Inject() extends Mappings {

  def apply(representativeContactName: String): Form[Boolean] =
    Form(
      "value" ->
        boolean("isRepresentativeContactUkBased.error.required", "isRepresentativeContactUkBased.error.required", representativeContactName)
    )
}
