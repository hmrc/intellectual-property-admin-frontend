/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import forms.SelectTechnicalContactFormProvider
import models.{ContactOptions, NormalMode}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.SelectTechnicalContactView

class SelectTechnicalContactViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "selectTechnicalContact"
  val companyName = "companyName"
  val radioOptionsMin = Seq((ContactOptions.RepresentativeContact, "representativeContact"), (ContactOptions.LegalContact, "legalContact"))
  val radioOptionsMax = Seq((ContactOptions.RepresentativeContact, "representativeContact"), (ContactOptions.LegalContact, "legalContact"),
    (ContactOptions.SecondaryLegalContact, "secondaryLegalContact"))
  val headingErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > ul > li > a"
  val radioErrorMessage = "#value-error"


  val form: Form[ContactOptions] = new SelectTechnicalContactFormProvider()()
  val view: SelectTechnicalContactView = injectInstanceOf[SelectTechnicalContactView]()

  "SelectTechnicalContactView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, "{0}", Seq.empty)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    "For only a representative and legal contact" should {
      implicit val doc: Document = asDocument(view(form, NormalMode, afaId, companyName, radioOptionsMin)(fakeRequest, messages))

      "display the correct number of radio options" in {
        assertRenderedById(doc, "value-representativeContact")
        assertRenderedById(doc, "value-legalContact")
        assertRenderedById(doc, "someone-else")
      }

      "have the correct value for the first radio button" in {
        element("#value-representativeContact").attr("value") mustBe "representativeContact"
      }
      "have the correct label for the first radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(1) > label").attr("for") mustBe "value-representativeContact"
      }
      "have the correct value for the second radio button" in {
        element("#value-legalContact").attr("value") mustBe "legalContact"
      }
      "have the correct label for the second radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(2) > label").attr("for") mustBe "value-legalContact"
      }
      "have the 'or' text before the final radio button" in {
        elementText("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div.govuk-radios__divider") mustEqual "or"
      }
      "have the correct value for the someone-else radio button" in {
        element("#someone-else").attr("value") mustBe "someoneElse"
      }
      "have the correct label for the someone-else radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(4) > label").attr("for") mustBe "someone-else"
      }
    }

    "For a representative, legal and otherLegal contact" should {
        implicit val doc: Document = asDocument(view(form, NormalMode, afaId, companyName, radioOptionsMax)(fakeRequest, messages))

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
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(1) > label").attr("for") mustBe "value-representativeContact"
      }
      "have the correct value for the second radio button" in {
        element("#value-legalContact").attr("value") mustBe "legalContact"
      }
      "have the correct label for the second radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(2) > label").attr("for") mustBe "value-legalContact"
      }
      "have the correct value for the third radio button" in {
        element("#value-secondaryLegalContact").attr("value") mustBe "secondaryLegalContact"
      }
      "have the correct label for the third radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(3) > label").attr("for") mustBe "value-secondaryLegalContact"
      }
      "have the 'or' text before the final radio button" in {
        elementText("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div.govuk-radios__divider") mustEqual "or"
      }
      "have the correct value for the someone-else radio button" in {
        element("#someone-else").attr("value") mustBe "someoneElse"
      }
      "have the correct label for the someone-else radio button" in {
        element("#main-content > div > div > form > div.govuk-form-group > fieldset > div > div:nth-child(5) > label").attr("for") mustBe "someone-else"
      }
    }

    "rendered with form error no options selected" must {
      lazy implicit val document: Document = asDocument(view(form.bind(
        Map("value" -> "")),NormalMode, afaId, companyName, radioOptionsMax)(fakeRequest, messages))

      "display the correct document title" in {
        document.title mustBe "Error: Select the technical contact - Protect intellectual property rights"
      }

      "display the correct error message in the heading" in {
        element(headingErrorMessage).text mustBe "Select a technical contact"
      }

      "error message must go to radio option" in {
        element(headingErrorMessage).attr("href") mustBe s"#value-${radioOptionsMax.head._1.toString}"
      }

      "display the correct error message in the radio options" in {
        element(radioErrorMessage).text mustBe "Error: Select a technical contact"
      }
    }
  }
}
