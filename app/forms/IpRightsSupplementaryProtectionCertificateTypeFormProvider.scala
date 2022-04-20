/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.IpRightsSupplementaryProtectionCertificateType

class IpRightsSupplementaryProtectionCertificateTypeFormProvider @Inject() extends Mappings {

  def apply(): Form[IpRightsSupplementaryProtectionCertificateType] =
    Form(
      "value" -> enumerable[IpRightsSupplementaryProtectionCertificateType]("ipRightsSupplementaryProtectionCertificateType.error.required")
    )
}
