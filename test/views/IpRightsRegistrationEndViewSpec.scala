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

import forms.IpRightsRegistrationEndFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.DateHintHelper
import views.behaviours.DateViewBehaviours
import views.html.IpRightsRegistrationEndView
import java.time.LocalDate

class IpRightsRegistrationEndViewSpec extends DateViewBehaviours {

  val messageKeyPrefix = "ipRightsRegistrationEnd"

  val rightType = "design"

  val form = new IpRightsRegistrationEndFormProvider()(Seq(rightType))

  "IpRightsRegistrationEndView view" must {

    val view = injectInstanceOf[IpRightsRegistrationEndView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, afaId, rightType)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(rightType),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like datePage(form, applyView, messageKeyPrefix, Seq(DateHintHelper.dateInFuture(LocalDate.now)))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
