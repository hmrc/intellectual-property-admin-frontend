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
import forms.UnlockAfaFormProvider
import models.{Lock, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.UnlockAfaView

class UnlockAfaViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "unlockAfa"

  val form = new UnlockAfaFormProvider()()

  val existingLock = Lock(afaId, "id", "name")

  val rightsHolder = "rights holder"

  "UnlockAfa view" must {

    val view = injectInstanceOf[UnlockAfaView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, afaId, existingLock.name, Some(rightsHolder))(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(rightsHolder),
      afaIdInHeader = false,
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.UnlockAfaController.onSubmit(afaId).url,
      args = Seq(rightsHolder),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithGuidanceWithParameter(applyView(form), "unlockAfa.guidance", existingLock.name)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
