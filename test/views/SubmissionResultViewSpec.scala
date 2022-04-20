/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import models.UserAnswers
import org.jsoup.nodes.Document
import viewmodels.SubmissionResult
import views.behaviours.ViewBehaviours
import views.html.SubmissionResultView

class SubmissionResultViewSpec extends ViewBehaviours {

  "SubmissionResult view" must {

    val afaId = userAnswersId

    val applicantCompanyName = "company"
    val expirationDate = "1 January 2019"

    val view = injectInstanceOf[SubmissionResultView](Some(UserAnswers(afaId)))

    val submissionResult = SubmissionResult(afaId, applicantCompanyName, expirationDate)

    val applyView = view.apply(submissionResult)(fakeRequest, messages)

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
