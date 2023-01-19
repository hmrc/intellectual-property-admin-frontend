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
import forms.IsApplicantLegalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsApplicantLegalContactUkBasedView

class IsApplicantLegalContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isApplicantLegalContactUkBased"

  val rightsHolderContactName = "foo"

  val form = new IsApplicantLegalContactUkBasedFormProvider()(rightsHolderContactName)

  "IsApplicantLegalContactUkBased view" must {

    val view = injectInstanceOf[IsApplicantLegalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, rightsHolderContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.IsApplicantLegalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
