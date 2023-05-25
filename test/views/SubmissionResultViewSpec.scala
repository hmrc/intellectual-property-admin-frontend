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

import models.UserAnswers
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import viewmodels.SubmissionResult
import views.behaviours.ViewBehaviours
import views.html.SubmissionResultView

class SubmissionResultViewSpec extends ViewBehaviours {

  "SubmissionResult view" must {

    val afaId = userAnswersId

    val applicantCompanyName = "company"
    val expirationDate       = "1 January 2019"

    val view = injectInstanceOf[SubmissionResultView](Some(UserAnswers(afaId)))

    val submissionResult = SubmissionResult(afaId, applicantCompanyName, expirationDate)

    val request = FakeRequest()

    val applyView = view.apply(submissionResult)(messages, request)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView, "submissionResult", afaIdInHeader = false)

    implicit val doc: Document = asDocument(applyView)

    "Confirmation panel" must {
      "display the correct submission result heading" in {
        doc.getElementsByClass("govuk-panel").size() mustBe 1
        element("#main-content > div > div > div.govuk-panel.govuk-panel--confirmation > h1").text() mustBe
          "Application published"
      }
      "display the correct applicant name and reference" in {
        element("#main-content > div > div > div.govuk-panel.govuk-panel--confirmation > div").text() mustBe
          s"Reference for $applicantCompanyName $afaId"
      }
    }

    "display the correct submission guidance" in {
      element("#main-content > div > div > p").text() mustBe
        s"This will protect intellectual property rights for $applicantCompanyName at the UK border until $expirationDate."
    }

    behave like pageWithGoHomeLink(applyView)
  }
}
