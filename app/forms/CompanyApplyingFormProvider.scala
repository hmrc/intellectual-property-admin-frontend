/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.CompanyApplying

class CompanyApplyingFormProvider @Inject() extends Mappings {

  val maxLength: Int = 200

  def apply(): Form[CompanyApplying] = Form(
    mapping(
      "companyName"    -> text("companyApplying.error.companyName.required")
        .verifying(maxLength(maxLength, "companyApplying.error.companyName.length")),
      "companyAcronym" -> optional(
        Forms.text
          .verifying(maxLength(maxLength, "companyApplying.error.companyAcronym.length"))
      )
    )(CompanyApplying.apply)(CompanyApplying.unapply)
  )
}
