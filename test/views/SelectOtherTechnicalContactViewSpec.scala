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

import forms.SelectOtherTechnicalContactFormProvider
import models.{ContactOptions, NormalMode}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.SelectOtherTechnicalContactView

class SelectOtherTechnicalContactViewSpec extends ViewBehaviours {

  val messageKeyPrefix    = "selectOtherTechnicalContact"
  val companyName         = "companyName"
  val radioOptionsMin     =
    Seq((ContactOptions.RepresentativeContact, "repContact"), (ContactOptions.LegalContact, "legalContact"))
  val radioOptionsMax     = Seq(
    (ContactOptions.RepresentativeContact, "repContact"),
    (ContactOptions.LegalContact, "legalContact"),
    (ContactOptions.SecondaryLegalContact, "otherLegalContact")
  )
  val headingErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > div > ul > li > a"
  val radioErrorMessage   = "#value-error"

  val form: Form[ContactOptions]            = new SelectOtherTechnicalContactFormProvider()()
  val view: SelectOtherTechnicalContactView = injectInstanceOf[SelectOtherTechnicalContactView]()

  "SelectOtherTechnicalContactView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, "{0}", Seq.empty)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    "For only a representative and legal contact" should {
      implicit val doc: Document =
        asDocument(view(form, NormalMode, afaId, companyName, radioOptionsMin)(fakeRequest, messages))

      "display the correct number of radio options" in {
        assertRenderedById(doc, "value-representativeContact")
        assertRenderedById(doc, "value-legalContact")
        assertRenderedById(doc, "someone-else")
      }

      "have the correct value for the first radio button" in {
        element("#value-representativeContact").attr("value") mustBe "representativeContact"
      }
      "have the correct label for the first radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(1) > label").attr(
          "for"
        ) mustBe "value-representativeContact"
      }
      "have the correct value for the second radio button" in {
        element("#value-legalContact").attr("value") mustBe "legalContact"
      }
      "have the correct label for the second radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(2) > label").attr(
          "for"
        ) mustBe "value-legalContact"
      }
      "have the 'or' text before the final radio button" in {
        elementText("div.govuk-radios__divider") mustEqual "or"
      }
      "have the correct value for the someone-else radio button" in {
        element("#someone-else").attr("value") mustBe "someoneElse"
      }
      "have the correct label for the someone-else radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(4) > label").attr("for") mustBe "someone-else"
      }
    }

    "For a representative, legal and otherLegal contact" should {
      implicit val doc: Document =
        asDocument(view(form, NormalMode, afaId, companyName, radioOptionsMax)(fakeRequest, messages))

      "display the correct number of radio options" in {
        assertRenderedById(doc, "value-representativeContact")
        assertRenderedById(doc, "value-legalContact")
        assertRenderedById(doc, "value-secondaryLegalContact")
        assertRenderedById(doc, "someone-else")
      }

      "have the correct value for the first radio button" in {
        element("#value-representativeContact").attr("value") mustBe "representativeContact"
      }
      "have the correct label for the first radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(1) > label").attr(
          "for"
        ) mustBe "value-representativeContact"
      }
      "have the correct value for the second radio button" in {
        element("#value-legalContact").attr("value") mustBe "legalContact"
      }
      "have the correct label for the second radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(2) > label").attr(
          "for"
        ) mustBe "value-legalContact"
      }
      "have the correct value for the third radio button" in {
        element("#value-secondaryLegalContact").attr("value") mustBe "secondaryLegalContact"
      }
      "have the correct label for the third radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(3) > label").attr(
          "for"
        ) mustBe "value-secondaryLegalContact"
      }
      "have the 'or' text before the final radio button" in {
        elementText("div.govuk-radios__divider") mustEqual "or"
      }
      "have the correct value for the someone-else radio button" in {
        element("#someone-else").attr("value") mustBe "someoneElse"
      }
      "have the correct label for the someone-else radio button" in {
        element("div.govuk-form-group > fieldset > div > div:nth-child(5) > label").attr("for") mustBe "someone-else"
      }
    }

    "when no options are selected" should {
      lazy implicit val document: Document = asDocument(
        view(form.bind(Map("value" -> "")), NormalMode, afaId, companyName, radioOptionsMax)(fakeRequest, messages)
      )

      "display the correct document title" in {
        document.title mustBe "Error: Select the other technical contact - Protect intellectual property rights"
      }

      "display there is a problem message" in {
        element(
          "#main-content > div > div > form > div.govuk-error-summary > div > h2"
        ).text mustBe "There is a problem"
      }

      "error message must go to radio option" in {
        element(headingErrorMessage).attr("href") mustBe s"#value-${radioOptionsMax.head._1.toString}"
      }

      "display instruction to solve the problem" in {
        element(headingErrorMessage).text mustBe "Select a technical contact"
      }

      "display the correct error message in the heading" in {
        element(radioErrorMessage).text mustBe "Error: Select a technical contact"
      }
    }
  }
}
