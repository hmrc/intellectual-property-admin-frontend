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
import forms.IsRepresentativeContactLegalContactFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsRepresentativeContactLegalContactView

class IsRepresentativeContactLegalContactViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isRepresentativeContactLegalContact"

  val form = new IsRepresentativeContactLegalContactFormProvider()()

  "IsRepresentativeContactLegalContact view" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[IsRepresentativeContactLegalContactView]

    val representativecontactName = "representativeContactName"

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, representativecontactName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(representativecontactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.IsRepresentativeContactLegalContactController.onSubmit(NormalMode, afaId).url,
      Seq(representativecontactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))
  }
}
