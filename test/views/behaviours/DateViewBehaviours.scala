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

import org.jsoup.nodes.Document
import org.scalatest.BeforeAndAfterEach
import org.scalatest._
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import matchers.should.Matchers._

import java.time.LocalDate

trait DateViewBehaviours extends ViewBehaviours with BeforeAndAfterEach {

  object Selectors {
    val hintText         = "#value-hint"
    val firstLabel       = "#value > div:nth-of-type(1) .govuk-label"
    val secondLabel      = "#value > div:nth-of-type(2) .govuk-label"
    val thirdLabel       = "#value > div:nth-of-type(3) .govuk-label"
    val button           = ".govuk-button"
    val errorSummary     = ".govuk-error-summary"
    val errorSummaryLink = ".govuk-error-summary__body > ul > li > a"
    val href             = "href"
    val errorMessage     = ".govuk-error-message"

  }
  val errorText = "date error"
  // scalastyle:off method.length
  def datePage(
    form: Form[LocalDate],
    applyView: Form[LocalDate] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    hintArgs: Seq[String] = Seq.empty,
    hintText: Option[String] = None
  ): Unit = {

    "behave like a date page"               must {
      lazy implicit val document: Document = asDocument(applyView(form))
      "no errors" in {
        elementNotExist(Selectors.errorSummary)
      }

      if (hintText.isDefined) {
        "have the correct hint text" in {
          elementText(Selectors.hintText) shouldBe messages(messageKeyPrefix + ".hint", hintArgs)
        }
      }

      "have the correct field labels day" in {
        elementText(Selectors.firstLabel) shouldBe "Day"
      }
      "have the correct field labels month" in {
        elementText(Selectors.secondLabel) shouldBe "Month"
      }
      "have the correct field labels year" in {
        elementText(Selectors.thirdLabel) shouldBe "Year"
      }
      "have the correct button text" in {
        elementText(Selectors.button) shouldBe "Save and continue"
      }
    }
    "behave like a date page with an error" must {
      lazy implicit val documentWithError: Document =
        asDocument(applyView(form.withError(FormError("value", errorText))))
      "no errors" in {
        element(Selectors.errorSummary)
      }
      if (hintText.isDefined) {
        "have the correct hint text" in {
          elementText(Selectors.hintText) shouldBe messages(messageKeyPrefix + ".hint", hintArgs)
        }
      }
      "have link to error field" in {
        val link = element(Selectors.errorSummaryLink)
        link.attr(Selectors.href) shouldBe "#value.day"
        link.text()               shouldBe errorText
      }
      "have error on the field" in {
        val error = element(Selectors.errorMessage)
        error.text shouldBe "Error: " + errorText
      }
      "have the correct field labels day" in {
        elementText(Selectors.firstLabel) shouldBe "Day"
      }
      "have the correct field labels month" in {
        elementText(Selectors.secondLabel) shouldBe "Month"
      }
      "have the correct field labels year" in {
        elementText(Selectors.thirdLabel) shouldBe "Year"
      }
      "have the correct button text" in {
        elementText(Selectors.button) shouldBe "Save and continue"
      }
    }
  }
  // scalastyle:on method.length
}
