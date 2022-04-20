/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import forms.IpRightsTypeFormProvider
import models.{IpRightsType, NormalMode}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.IpRightsTypeView

class IpRightsTypeViewSpec extends QuestionViewBehaviours[IpRightsType] {

  val messageKeyPrefix = "ipRightsType"
  val headingErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > ul > li > a"
  val radioErrorMessage = "#value-error"


  val form = new IpRightsTypeFormProvider()()

  val view = injectInstanceOf[IpRightsTypeView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, 0, afaId)(fakeRequest, messages)

  "IpRightsTypeView" must {

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }

  def radioButtonSelector(radioButtonIndex: Int) : String =
    s"#main-content > div > div > form > div > fieldset > div > div:nth-child($radioButtonIndex)"


  "IprTypeView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- IpRightsType.radioItems(form)) {
          assertContainsRadioButton(doc, option.id.get, "value", option.value.get, isChecked = false)
        }
      }
    }

    for (option <- IpRightsType.radioItems(form)) {

      s"rendered with a value of '${option.value.get}'" must {

        s"have the '${option.value.get}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value.get}"))))

          assertContainsRadioOption(doc, option.id.get, "value", option.value.get, "radio", isChecked = true)

          for (unselectedOption <- IpRightsType.radioItems(form).filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id.get, "value", unselectedOption.value.get, isChecked = false)
          }
        }
      }
    }

    "contain the correct radio item label" must {
      implicit val doc: Document = asDocument(applyView(form))

      "radio item must be trademark" in {
        elementText(radioButtonSelector(1)) mustBe "Trade mark"
      }

      "radio item must be copyright" in {
        elementText(radioButtonSelector(2)) mustBe "Copyright"
      }

      "radio item must be design" in {
        elementText(radioButtonSelector(3)) mustBe "Design"
      }

      "radio item must be patent" in {
        elementText(radioButtonSelector(4)) mustBe "Patent"
      }

      "radio item must be plant variety" in {
        elementText(radioButtonSelector(5)) mustBe "Plant variety"
      }

      "radio item must be geographical indication" in {
        elementText(radioButtonSelector(6)) mustBe "Geographical indication"
      }

      "radio item must be supplementary protection certificate" in {
        elementText(radioButtonSelector(7)) mustBe "Supplementary protection certificate"
      }

      "radio item must be semiconductor topography" in {
        elementText(radioButtonSelector(8)) mustBe "Topography of a semiconductor product"
      }
    }

    "rendered with form error no options selected" must {

      lazy implicit val document: Document = asDocument(view(form.bind(
        Map("value" -> "")), NormalMode, 0, afaId)(fakeRequest, messages))

      "display the correct document title" in {
        document.title mustBe "Error: What right do they want to add to this application? - Protect intellectual property rights"
      }

      "display the correct error message in the heading" in {
        element(headingErrorMessage).text mustBe "Select what right they want to add to this application"
      }

      "error message must go to radio option" in {
        element(headingErrorMessage).attr("href") mustBe "#ipRightsType.trademark"
      }

      "display the correct error message in the radio options" in {
        element(radioErrorMessage).text mustBe "Error: Select what right they want to add to this application"
      }
    }
  }
}
