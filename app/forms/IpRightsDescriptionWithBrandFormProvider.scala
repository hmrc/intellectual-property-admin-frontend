/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.IpRightsDescriptionWithBrand

class IpRightsDescriptionWithBrandFormProvider @Inject() extends Mappings {

  val brandMaxLength: Int = 100
  val descriptionMaxLength: Int = 1000

   def apply(): Form[IpRightsDescriptionWithBrand] = Form(
     mapping(
      "brand" -> text("ipRightsDescriptionWithBrand.error.brand.required")
        .verifying(maxLength(brandMaxLength, "ipRightsDescriptionWithBrand.error.brand.length")),
      "value" -> text("ipRightsDescriptionWithBrand.error.description.required")
        .verifying(maxLength(descriptionMaxLength, "ipRightsDescriptionWithBrand.error.description.length"))
    )(IpRightsDescriptionWithBrand.apply)(IpRightsDescriptionWithBrand.unapply)
   )
 }
