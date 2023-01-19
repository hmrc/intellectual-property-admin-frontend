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

package views.behavioursDesignSystem

import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours

trait TextInputViewBehaviours[A] extends ViewBehaviours {

  object Selectors {
    val errorSummary           = ".govuk-error-summary"
    val inputErrorClassName    = "govuk-input--error"
    val govUKButtonClass       = ".govuk-button"
    val errorSummaryLink       = ".govuk-error-summary__body > ul > li > a"
    val textAreaErrorClassName = "govuk-textarea--error"
    val href                   = "href"
    val form                   = "form"
  }

  val errorMessage: String = "test error message"
  val form: Form[A]

  // scalastyle:off method.length
  def pageWithTextInputs(
    form: Form[A],
    createView: Form[A] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    textInputIds: Seq[String],
    argsUsedInBrowserTitle: Boolean = false,
    titleArgs: Seq[String] = Seq.empty
  ): Unit =
    "behave like a page with one or more text inputs" when {
      "rendered" must {
        lazy implicit val doc: Document = asDocument(createView(form))
        "not render an error summary" in {
          elementNotExist(Selectors.errorSummary)
        }
        "contain the expected form action" in {
          element(Selectors.form).attr("action") mustBe expectedFormAction
        }
        for (inputId <- textInputIds) {
          s"contain an input for $inputId" in {
            assertRenderedById(doc, inputId)
          }
          s"not have an error class associated with the input for $inputId" in {
            val errorInput = doc.getElementById(inputId)
            errorInput.hasClass(Selectors.inputErrorClassName) mustBe false
          }
        }
      }

      "rendered with any error" must {
        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(FormError("value", "Some error message"))))
          if (argsUsedInBrowserTitle) {
            assertEqualsValue(
              doc,
              "title",
              s"""${messages("error.browser.title.prefix")} ${messages(
                  s"$messageKeyPrefix.title",
                  titleArgs: _*
                )} - ${messages("site.service_name")}"""
            )
          } else {
            assertEqualsValue(
              doc,
              "title",
              s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")} - ${messages(
                  "site.service_name"
                )}"""
            )
          }
        }
      }

      for (inputId <- textInputIds) {
        implicit val doc: Document = asDocument(createView(form.withError(FormError(inputId, errorMessage))))
        s"rendered with an error for input with id '$inputId'" must {
          "show an error summary" in {
            assertRenderedByCssSelector(doc, Selectors.errorSummary)
          }
          "have link to the input in error with the correct error message" in {
            val link = element(Selectors.errorSummaryLink)
            link.attr(Selectors.href) mustBe s"#$inputId"
            link.text() mustBe errorMessage
          }
          s"have an error label for the error input with id '$inputId'" in {
            val errorInput = doc.getElementById(inputId)
            errorInput.hasClass(Selectors.inputErrorClassName) mustBe true
            val errorSpan  = doc.getElementById(s"$inputId-error")
            errorSpan.text mustBe messages(s"Error: $errorMessage")
          }
        }
      }
    }

  def pageWithTextArea(
    form: Form[A],
    createView: Form[A] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    textInputIds: Seq[String],
    argsUsedInBrowserTitle: Boolean = false,
    titleArgs: Seq[String] = Seq.empty
  ): Unit =
    "behave like a page with one or more text areas" when {
      "rendered" must {
        lazy implicit val doc: Document = asDocument(createView(form))
        "not render an error summary" in {
          elementNotExist(Selectors.errorSummary)
        }

        "contain the expected form action" in {
          element(Selectors.form).attr("action") mustBe expectedFormAction
        }
        for (inputId <- textInputIds) {
          s"contain an input for $inputId" in {
            assertRenderedById(doc, inputId)
          }
          s"not have an error class associated with the input for $inputId" in {
            val errorInput = doc.getElementById(inputId)
            errorInput.hasClass(Selectors.textAreaErrorClassName) mustBe false
          }
        }
      }

      "rendered with any error" must {
        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(FormError("value", "Some error message"))))
          if (argsUsedInBrowserTitle) {
            assertEqualsValue(
              doc,
              "title",
              s"""${messages("error.browser.title.prefix")} ${messages(
                  s"$messageKeyPrefix.title",
                  titleArgs: _*
                )} - ${messages("site.service_name")}"""
            )
          } else {
            assertEqualsValue(
              doc,
              "title",
              s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")} - ${messages(
                  "site.service_name"
                )}"""
            )
          }
        }
      }

      for (inputId <- textInputIds) {
        implicit val doc: Document = asDocument(createView(form.withError(FormError(inputId, errorMessage))))
        s"rendered with an error for input with id '$inputId'" must {
          "show an error summary" in {
            assertRenderedByCssSelector(doc, Selectors.errorSummary)
          }
          "have link to the input in error with the correct error message" in {
            val link = element(Selectors.errorSummaryLink)
            link.attr(Selectors.href) mustBe s"#$inputId"
            link.text() mustBe errorMessage
          }
          s"have an error label for the error input with id '$inputId'" in {
            val errorInput = doc.getElementById(inputId)
            errorInput.hasClass(Selectors.textAreaErrorClassName) mustBe true
            val errorSpan  = doc.getElementById(s"$inputId-error")
            errorSpan.text mustBe messages(s"Error: $errorMessage")
          }
        }
      }
    }
  // scalastyle:on method.length
}
