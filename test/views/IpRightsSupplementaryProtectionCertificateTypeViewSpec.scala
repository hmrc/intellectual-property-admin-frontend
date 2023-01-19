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

import forms.IpRightsSupplementaryProtectionCertificateTypeFormProvider
import models.{IpRightsSupplementaryProtectionCertificateType, NormalMode, UserAnswers}
import org.jsoup.nodes.Document
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.IpRightsSupplementaryProtectionCertificateTypeView

class IpRightsSupplementaryProtectionCertificateTypeViewSpec extends ViewBehaviours {

  val messageKeyPrefix    = "ipRightsSupplementaryProtectionCertificateType"
  val headingErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > ul > li > a"
  val radioErrorMessage   = "#value-error"

  val form = new IpRightsSupplementaryProtectionCertificateTypeFormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId))).build()

  val view: IpRightsSupplementaryProtectionCertificateTypeView =
    application.injector.instanceOf[IpRightsSupplementaryProtectionCertificateTypeView]

  val index = 0

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode, index, afaId)(fakeRequest, messages)

  "IpRightsSupplementaryProtectionCertificateTypeView" must {

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    def radioButtonSelector(radioButtonIndex: Int): String =
      s"#main-content > div > div > form > div > fieldset > div > div:nth-child($radioButtonIndex)"

    "rendered" must {

      "contain radio buttons for the value" in {
        val doc: Document = asDocument(applyView(form))

        for (option <- IpRightsSupplementaryProtectionCertificateType.radioItems(form))
          assertContainsRadioButton(doc, option.id.get, "value", option.value.get, isChecked = false)
      }

      "contain the correct radio item label" must {
        implicit val doc = asDocument(applyView(form))

        "radio item must be medicinal" in {
          elementText(radioButtonSelector(1)) mustBe "Medicinal products"
        }

        "radio item must be plant protection" in {
          elementText(radioButtonSelector(2)) mustBe "Plant protection products"
        }
      }
    }

    for (option <- IpRightsSupplementaryProtectionCertificateType.radioItems(form))
      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value.get}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value.get}"))))

          assertContainsRadioOption(doc, option.id.get, "value", option.value.get, "radio", isChecked = true)

          for (
            unselectedOption <-
              IpRightsSupplementaryProtectionCertificateType.radioItems(form).filterNot(o => o == option)
          )
            assertContainsRadioButton(
              doc,
              unselectedOption.id.get,
              "value",
              unselectedOption.value.get,
              isChecked = false
            )
        }
      }

    lazy implicit val document =
      asDocument(view(form.bind(Map("value" -> "")), NormalMode, 0, afaId)(fakeRequest, messages))

    "display the correct document title" in {
      document.title mustBe "Error: What does this certificate cover? - Protect intellectual property rights"
    }

    "display the correct error message in the heading" in {
      element(headingErrorMessage).text mustBe "Select what this certificate covers"
    }

    "error message must go to radio option" in {
      element(headingErrorMessage).attr("href") mustBe "#ipRightsSupplementaryProtectionCertificateType.medicinal"
    }

    "display the correct error message in the radio options" in {
      element(radioErrorMessage).text mustBe "Error: Select what this certificate covers"
    }
  }
}
