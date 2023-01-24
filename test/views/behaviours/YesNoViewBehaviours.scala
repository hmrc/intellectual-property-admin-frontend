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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait YesNoViewBehaviours extends QuestionViewBehaviours[Boolean] {

  def yesNoPageUsingDesignSystem(
    form: Form[Boolean],
    createView: Form[Boolean] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    args: Seq[Any] = Seq.empty,
    argsUsedInBrowserTitle: Boolean = false
  ): Unit =
    "behave like a page with a Yes/No question" when {
      "rendered"                       must {
        lazy implicit val doc = asDocument(createView(form))
        "contain a legend for the question" in {
          val legends = doc.getElementsByTag("legend")
          legends.size mustBe 1
          legends.first.text mustBe messages(s"$messageKeyPrefix.heading", args: _*)
        }
        "contain an input for the value" in {
          assertRenderedById(doc, "value-yes")
          assertRenderedById(doc, "value-no")
        }
        "have no values checked when rendered with no form" in {
          assert(!doc.getElementById("value-yes").hasAttr("checked"))
          assert(!doc.getElementById("value-no").hasAttr("checked"))
        }
        "not render an error summary" in {
          assertNotRenderedByClass(doc, "govuk-error-summary__title")
        }
        "contain the correct form action" in {
          element("form").attr("action") mustBe expectedFormAction
        }
      }
      "rendered with a value of true"  must {
        behave like answeredYesNoPageUsingDesignSystem(createView, true)
      }
      "rendered with a value of false" must {
        behave like answeredYesNoPageUsingDesignSystem(createView, false)
      }

      "rendered with an error" must {
        lazy implicit val doc = asDocument(createView(form.withError(error)))
        "show an error summary" in {
          assertRenderedByClass(doc, "govuk-error-summary__title")
        }

        "show an error in the value field's label" in {
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe "Error: " + messages(errorMessage)
        }

        "error message must go to radio option" in {
          element("#main-content > div > div > form > div.govuk-error-summary > div > div > ul > li > a").attr(
            "href"
          ) mustBe "#value-yes"
        }

        "show an error prefix in the browser title" in {
          val title = doc.getElementsByTag("title").first.text
          if (argsUsedInBrowserTitle) {
            title mustEqual s"""${messages("error.browser.title.prefix")} ${messages(
                s"$messageKeyPrefix.title",
                args: _*
              )} - ${messages("site.service_name")}"""
          } else {
            title mustEqual s"""${messages("error.browser.title.prefix")} ${messages(
                s"$messageKeyPrefix.title"
              )} - ${messages("site.service_name")}"""
          }
        }
      }
    }

  def answeredYesNoPageUsingDesignSystem(createView: Form[Boolean] => HtmlFormat.Appendable, answer: Boolean): Unit = {

    "have only the correct value checked" in {
      val doc = asDocument(createView(form.fill(answer)))
      assert(doc.getElementById("value-yes").hasAttr("checked") == answer)
      assert(doc.getElementById("value-no").hasAttr("checked") != answer)
    }

    "not render an error summary" in {
      val doc = asDocument(createView(form.fill(answer)))
      assertNotRenderedByClass(doc, "govuk-error-summary__title")
    }
  }
}
