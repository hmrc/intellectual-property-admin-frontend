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
import forms.PermissionToDestroySmallConsignmentsFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.PermissionToDestroySmallConsignmentsView

class PermissionToDestroySmallConsignmentsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "permissionToDestroySmallConsignments"

  val form = new PermissionToDestroySmallConsignmentsFormProvider()()

  "PermissionToDestroySmallConsignments view" must {

    val view = injectInstanceOf[PermissionToDestroySmallConsignmentsView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.PermissionToDestroySmallConsignmentsController.onSubmit(NormalMode, afaId).url
    )

    behave like pageWithGuidance(
      applyView(form),
      "permissionToDestroySmallConsignments.paragraph",
      Seq("reason", "applicability")
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
