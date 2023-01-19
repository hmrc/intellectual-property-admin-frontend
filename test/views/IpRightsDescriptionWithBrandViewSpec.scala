/*
 * Copyright 2023 HM Revenue & Customs
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

package views

import controllers.routes
import forms.IpRightsDescriptionWithBrandFormProvider
import models.{IpRightsDescriptionWithBrand, NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.IpRightsDescriptionWithBrandView

class IpRightsDescriptionWithBrandViewSpec extends TextInputViewBehaviours[IpRightsDescriptionWithBrand] {

  val messageKeyPrefix = "ipRightsDescriptionWithBrand"

  override val form = new IpRightsDescriptionWithBrandFormProvider()()

  "IpRightsDescriptionWithBrandView" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[IpRightsDescriptionWithBrandView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.IpRightsDescriptionWithBrandController.onSubmit(NormalMode, 0, afaId).url,
      Seq("brand")
    )

    behave like pageWithTextArea(
      form,
      applyView,
      messageKeyPrefix,
      routes.IpRightsDescriptionWithBrandController.onSubmit(NormalMode, 0, afaId).url,
      Seq("value")
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
