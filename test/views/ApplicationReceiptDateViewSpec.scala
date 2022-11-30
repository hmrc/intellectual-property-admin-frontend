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

import forms.ApplicationReceiptDateFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateViewBehaviours
import views.html.ApplicationReceiptDateView

class ApplicationReceiptDateViewSpec extends DateViewBehaviours {

  val messageKeyPrefix = "applicationReceiptDate"
  val title: String    = "When did HMRC receive the application? - Protect intellectual property rights"
  val heading: String  = "When did HMRC receive the application?"

  val form = new ApplicationReceiptDateFormProvider()()

  "ApplicationReceiptDateView view" must {

    val view = injectInstanceOf[ApplicationReceiptDateView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like datePage(form, applyView, messageKeyPrefix, Seq("7 7 2021"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
