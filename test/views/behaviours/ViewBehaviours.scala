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

import config.FrontendAppConfig
import org.jsoup.nodes.Element
import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  def normalPage(
    view: HtmlFormat.Appendable,
    messageKeyPrefix: String,
    args: Seq[Any] = Seq.empty,
    afaIdInHeader: Boolean = true,
    argsUsedInBrowserTitle: Boolean = false
  ): Unit =
    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc  = asDocument(view)
          val nav  = doc.getElementById("proposition-menu")
          val span = nav.children.first

          span.text mustEqual messages("site.service_name")
        }

        "display the correct browser title" in {

          val doc   = asDocument(view)
          val title = doc.getElementsByTag("title").first.text

          if (argsUsedInBrowserTitle) {
            title mustEqual messages(s"$messageKeyPrefix.title", args: _*) + s" - ${messages("site.service_name")}"
          } else {
            title mustEqual messages(s"$messageKeyPrefix.title") + s" - ${messages("site.service_name")}"
          }
        }

        "display the correct page heading" in {

          val doc = asDocument(view)

          if (afaIdInHeader) {
            assertPageTitleWithAfaIdEqualsMessage(doc, s"$messageKeyPrefix.heading", args: _*)
          } else {
            assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", args: _*)
          }
        }

        "display accessibility footer link" in {
          val link = asDocument(view).select("#footer > div  > a")
          link.attr("href") mustBe frontendAppConfig.manageIprAccessibilityUrl
          link.text mustBe messages("accessibilityStatement.footerUrl.text")
        }

        "not display language toggles" in {

          val doc = asDocument(view)
          assertNotRenderedById(doc, "cymraeg-switch")
        }
      }
    }

  def normalPageUsingDesignSystem(
    appConfig: FrontendAppConfig,
    view: HtmlFormat.Appendable,
    messageKeyPrefix: String,
    args: Seq[Any] = Seq.empty,
    afaIdInHeader: Boolean = true,
    argsUsedInBrowserTitle: Boolean = false
  ): Unit =
    "behave like a normal page" when {

      "rendered" must {

        "display the correct browser title" in {
          val doc   = asDocument(view)
          val title = doc.getElementsByTag("title").first.text

          if (argsUsedInBrowserTitle) {
            title mustEqual messages(s"$messageKeyPrefix.title", args: _*) + s" - ${messages("site.service_name")}"
          } else {
            title mustEqual messages(s"$messageKeyPrefix.title") + s" - ${messages("site.service_name")}"
          }
        }

        "display accessibility footer link" in {
          val link = asDocument(view).select("footer > div > div > div > ul > li:nth-of-type(1) > a")
          link.attr("href") mustBe appConfig.manageIprAccessibilityUrl
          link.text mustBe messages("accessibilityStatement.footerUrl.text")
        }

        "display the correct page heading" in {
          val doc = asDocument(view)
          // ensure default true option is checked last
          if (afaIdInHeader) {
            assertAfaIdHeaderReferencePresent(doc)
          }
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", args: _*)
        }
      }
    }

  def pageWithBackLink(view: HtmlFormat.Appendable): Unit =
    "behave like a page with a back link" must {

      "have a back link" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }

  def pageWithGuidance(view: HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: Seq[String]): Unit =
    "display the correct guidance" in {

      val doc = asDocument(view)
      for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
    }

  def pageWithGuidanceWithParameter(view: HtmlFormat.Appendable, messageKey: String, messageParameter: String): Unit =
    "display the correct guidance" in {

      val doc = asDocument(view)
      assertContainsText(doc, messages(s"$messageKey", messageParameter))
    }

  def pageWithButtonLink(view: HtmlFormat.Appendable, messageKey: String, linkUrl: String): Unit = {

    "display a single button link with the correct message" in {

      val doc     = asDocument(view)
      val buttons = doc.getElementsByClass("button")

      buttons.size() mustEqual 1

      buttons.first().text mustEqual messages(messageKey)

    }

    "display a button with the correct navigation link" in {
      val doc     = asDocument(view)
      val buttons = doc.getElementsByClass("button")

      buttons.first().attr("href") mustEqual linkUrl
    }
  }

  def pageWithButtonLinkUsingDesignSystem(view: HtmlFormat.Appendable, messageKey: String, linkUrl: String): Unit = {

    val doc     = asDocument(view)
    val buttons = doc.getElementsByClass("govuk-button")

    "display a single button link with the correct message" in {

      buttons.size() mustEqual 1
      buttons.first().text mustEqual messages(messageKey)
    }

    "display a button with the correct navigation link" in {

      buttons.first().attr("href") mustEqual linkUrl
    }
  }

  def pageWithGoHomeLink(view: HtmlFormat.Appendable): Unit =
    "display a 'Go to Home' link with the correct navigation link" in {

      val doc = asDocument(view)

      val homeLink = doc.getElementById("manage-applications-home")

      homeLink.text mustEqual messages("goToManageApplicationsHome")

      homeLink.attr("href") mustEqual injectInstanceOf[FrontendAppConfig]().manageIprHomeUrl
    }

  def pageWithSubmitButtonAndGoHomeLink(view: HtmlFormat.Appendable): Unit = {

    "display a submit button link with the correct message" in {

      val doc             = asDocument(view)
      val button: Element = doc.getElementById("submit")

      button.text mustEqual messages("site.continue")

    }

    behave like pageWithGoHomeLink(view)

  }

  def pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(view: HtmlFormat.Appendable): Unit = {

    behave like pageWithSubmitButton(view)

    behave like pageWithGoHomeLink(view)
  }

  def pageWithSubmitButton(view: HtmlFormat.Appendable): Unit =
    "display a submit button with the correct message" in {

      val doc     = asDocument(view)
      val buttons = doc.getElementsByClass("govuk-button")

      buttons.first().text mustEqual "Save and continue"

    }

}
