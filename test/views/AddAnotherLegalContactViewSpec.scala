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

package views

import controllers.routes
import forms.AddAnotherLegalContactFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AddAnotherLegalContactView

class AddAnotherLegalContactViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addAnotherLegalContact"

  val form = new AddAnotherLegalContactFormProvider()()

  "AddAnotherLegalContact view" must {

    val view = injectInstanceOf[AddAnotherLegalContactView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.AddAnotherLegalContactController.onSubmit(NormalMode, afaId).url
    )

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
