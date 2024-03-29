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
import forms.IsSecondaryTechnicalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsSecondaryTechnicalContactUkBasedView

class IsSecondaryTechnicalContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix: String = "isSecondaryTechnicalContactUkBased"

  val secondaryTechnicalContactName: String = "foo"

  val form = new IsSecondaryTechnicalContactUkBasedFormProvider()(secondaryTechnicalContactName)

  "IsSecondaryTechnicalContactUkBased view" must {

    val view = injectInstanceOf[IsSecondaryTechnicalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, secondaryTechnicalContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(secondaryTechnicalContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.IsSecondaryTechnicalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(secondaryTechnicalContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
