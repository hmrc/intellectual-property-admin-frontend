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

import forms.CompanyApplyingIsRightsHolderFormProvider
import models.{CompanyApplyingIsRightsHolder, NormalMode, UserAnswers}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.CompanyApplyingIsRightsHolderView
import org.scalacheck.Arbitrary.arbitrary

class CompanyApplyingIsRightsHolderViewSpec extends QuestionViewBehaviours[CompanyApplyingIsRightsHolder] {

  val messageKeyPrefix    = "companyApplyingIsRightsHolder"
  val headingErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > div > ul > li > a"
  val radioErrorMessage   = "#value-error"

  def radioButtonSelector(radioButtonIndex: Int): String =
    s"#main-content > div > div > form > div > fieldset > div > div:nth-child($radioButtonIndex) > label"

  val form = new CompanyApplyingIsRightsHolderFormProvider()()

  val view = injectInstanceOf[CompanyApplyingIsRightsHolderView](Some(UserAnswers(afaId)))

  val companyName = arbitrary[String].sample.value

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, afaId, companyName)(fakeRequest, messages)

  "CompanyApplyingIsRightsHolderView" must {

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, Seq(companyName))

    behave like pageWithBackLink(applyView(form))
  }

  "CompanyApplyingIsRightsHolderView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- CompanyApplyingIsRightsHolder.radioItems(form))
          assertContainsRadioButton(doc, option.id.get, "value", option.value.get, false)
      }
    }

    for (option <- CompanyApplyingIsRightsHolder.radioItems(form))
      s"rendered with a value of '${option.value.get}'" must {

        s"have the '${option.value.get}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value.get}"))))

          assertContainsRadioOption(doc, option.id.get, "value", option.value.get, "radio", true)

          for (unselectedOption <- CompanyApplyingIsRightsHolder.radioItems(form).filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id.get, "value", unselectedOption.value.get, false)
        }
      }
  }

  behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  "contain the correct radio item label" must {
    implicit val doc: Document = asDocument(applyView(form))

    "radio item must be rights holder" in {
      elementText(radioButtonSelector(1)) mustBe "Rights holder"
    }

    "radio item must be rights management collective body" in {
      elementText(radioButtonSelector(2)) mustBe "IP rights management collective body"
    }

    "radio item must be authorised applicant" in {
      elementText(
        radioButtonSelector(3)
      ) mustBe "Person or entity formally authorised to both use and initiate proceedings to protect the rights"
    }
  }

  "rendered with form error no options selected" must {
    lazy implicit val document: Document =
      asDocument(view(form.bind(Map("value" -> "")), NormalMode, afaId, companyName)(fakeRequest, messages))

    "display the correct document title" in {
      document.title mustBe "Error: What is the status of the applicant? - Protect intellectual property rights"
    }

    "display the correct error message in the heading" in {
      element(headingErrorMessage).text mustBe "Select status of applicant"
    }

    "error message must go to radio option" in {
      element(headingErrorMessage).attr("href") mustBe "#companyApplyingIsRightsHolder.rightsHolder"
    }

    "display the correct error message in the radio options" in {
      element(radioErrorMessage).text mustBe "Error: Select status of applicant"
    }
  }
}
