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
import forms.DeleteDraftFormProvider
import models.UserAnswers
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.DeleteDraftView

class DeleteDraftViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "deleteDraft"

  val form = new DeleteDraftFormProvider()()

  val rightsHolder = "Rights Holder"

  "DeleteDraft view" must {

    val view = injectInstanceOf[DeleteDraftView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable = view.apply(form, afaId, rightsHolder)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, Seq(rightsHolder), argsUsedInBrowserTitle = true)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix,
      routes.DeleteDraftController.onSubmit(afaId).url, args = Seq(rightsHolder), argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
