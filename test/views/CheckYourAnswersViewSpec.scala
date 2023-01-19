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

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  private val view = injectInstanceOf[CheckYourAnswersView](Some(emptyUserAnswers))

  def applyView(buttonKey: String, noEvidence: Boolean): HtmlFormat.Appendable =
    view.apply(afaId = afaId, answerSections = Seq.empty, "", buttonKey, false, noEvidence, "company name")(messages)

  behave like normalPageUsingDesignSystem(frontendAppConfig, applyView("", false), "checkYourAnswers", Seq.empty, true)

  behave like pageWithGoHomeLink(applyView("", false))

  "When there is no evidence of power to act the green submit button" must {
    "read 'Confirm evidence of power to act'" in {
      val doc     = asDocument(applyView("checkYourAnswers.noEvidence", true))
      val buttons = doc.getElementsByClass("govuk-button")

      buttons.size() mustEqual 1
      buttons.first().text mustBe "Confirm evidence of power to act"
    }
  }

  "When there is evidence of power to act but the application is incomplete, the green submit button" must {
    "read 'Continue application'" in {
      val doc     = asDocument(applyView("checkYourAnswers.continue", false))
      val buttons = doc.getElementsByClass("govuk-button")

      buttons.size() mustEqual 1
      buttons.first().text mustBe "Continue application"
    }
  }
}
