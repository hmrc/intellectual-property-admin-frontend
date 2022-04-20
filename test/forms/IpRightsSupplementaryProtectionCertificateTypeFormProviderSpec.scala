/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.IpRightsSupplementaryProtectionCertificateType
import play.api.data.FormError

class IpRightsSupplementaryProtectionCertificateTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new IpRightsSupplementaryProtectionCertificateTypeFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "ipRightsSupplementaryProtectionCertificateType.error.required"

    behave like optionsField[IpRightsSupplementaryProtectionCertificateType](
      form,
      fieldName,
      validValues  = IpRightsSupplementaryProtectionCertificateType.values.toSet,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
