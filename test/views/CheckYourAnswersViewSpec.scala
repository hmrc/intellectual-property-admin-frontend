/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  private val view = injectInstanceOf[CheckYourAnswersView](Some(emptyUserAnswers))

  def applyView(buttonKey: String, noEvidence: Boolean): HtmlFormat.Appendable =
    view.apply(afaId = afaId, answerSections = Seq.empty, "", buttonKey, false, noEvidence, "company name")(fakeRequest, messages)

  behave like normalPageUsingDesignSystem(frontendAppConfig, applyView("", false), "checkYourAnswers", Seq.empty, true)

  behave like pageWithGoHomeLink(applyView("", false))

  "When there is no evidence of power to act the green submit button" must {
    "read 'Confirm evidence of power to act'" in {
      val doc = asDocument(applyView("checkYourAnswers.noEvidence", true))
      val buttons = doc.getElementsByClass("govuk-button")

      buttons.size() mustEqual 1
      buttons.first().text mustBe "Confirm evidence of power to act"
    }
  }

  "When there is evidence of power to act but the application is incomplete, the green submit button" must {
    "read 'Continue application'" in {
      val doc = asDocument(applyView("checkYourAnswers.continue", false))
      val buttons = doc.getElementsByClass("govuk-button")

      buttons.size() mustEqual 1
      buttons.first().text mustBe "Continue application"
    }
  }
}
