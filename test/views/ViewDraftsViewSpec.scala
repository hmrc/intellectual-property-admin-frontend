/*
 * Copyright 2022 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import viewmodels.DraftRow
import views.behaviours.ViewBehaviours
import views.html.ViewDraftsView

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewDraftsViewSpec extends ViewBehaviours {

  "ViewDrafts view" must {

    val view = injectInstanceOf[ViewDraftsView](Some(emptyUserAnswers))

    val applyView = view.apply(List.empty)(messages)

    val newCompany = "New Company"
    val oldCompany = "Old Business"

    val draft  = new DraftRow(Some(newCompany), afaId, Some(LocalDate.of(2020, 1, 1)), isLocked = false)
    val draft2 = new DraftRow(Some(oldCompany), afaId, None, isLocked = true)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView, "viewDrafts", afaIdInHeader = false)

    behave like pageWithBackLink(applyView)

    behave like pageWithGoHomeLink(applyView)

    def linkSelector(rowIndex: Int, columnIndex: Int, isLink: Boolean = false): String =
      if (!isLink) {
        s"#main-content > div > div > table > tbody > tr:nth-child($rowIndex) > td:nth-child($columnIndex)"
      } else {
        s"#main-content > div > div > table > tbody > tr:nth-child($rowIndex) > td:nth-child($columnIndex) > a"
      }

    "must show that there are no drafts when passed an empty list of drafts" in {

      val doc = Jsoup.parse(view(List.empty)(messages).toString)

      assertContainsMessages(doc, "viewDrafts.empty")
    }

    "show draft table" must {

      def nonEmptyView(): HtmlFormat.Appendable = view.apply(List(draft, draft2))(messages)

      lazy implicit val document: Document = asDocument(nonEmptyView())

      "show draft table is present" in {
        assertRenderedByCssSelector(document, "table")
      }

      "have correct company heading" in {
        elementText("#main-content > div > div > table > thead > tr > th:nth-child(1)") mustBe "Company"
      }

      "have correct reference heading" in {
        elementText("#main-content > div > div > table > thead > tr > th:nth-child(2)") mustBe "Reference"
      }

      "have correct publish by heading" in {
        elementText("#main-content > div > div > table > thead > tr > th:nth-child(3)") mustBe "Publish by"
      }

      "have correct continue heading" in {
        elementText("#main-content > div > div > table > thead > tr > th:nth-child(4)") mustBe "Continue"
      }

      "have correct delete heading" in {
        elementText("#main-content > div > div > table > thead > tr > th:nth-child(5)") mustBe "Delete"
      }

      "have correct company name for unlock draft" in {
        elementText(linkSelector(1, 1)) mustBe draft.companyName.get
      }

      "have correct company name for locked draft" in {
        elementText(linkSelector(2, 1)) mustBe draft2.companyName.get
      }

      "have correct reference for unlock draft" in {
        elementText(linkSelector(1, 2)) mustBe s"${draft.reference}"
      }

      "have correct reference for locked draft" in {
        elementText(linkSelector(2, 2)) mustBe s"${draft2.reference}"
      }

      "have correct publishBy date for unlock draft" in {
        elementText(linkSelector(1, 3)) mustBe draft.publishBy
          .map(_.format(DateTimeFormatter.ofPattern("d MMMM yyyy")))
          .get
      }

      "have correct publishBy date for locked draft" in {
        elementText(linkSelector(2, 3)) mustBe ""
      }

      "have the correct continue link" in {
        element(linkSelector(1, 4, true)).attr("href") mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(afaId).url
      }

      "have the correct continue link text" in {
        elementText(linkSelector(1, 4, true)) mustBe s"Continue draft application $afaId for $newCompany"
      }

      "have the correct unlock link" in {
        element(linkSelector(2, 4, true)).attr("href") mustBe
          controllers.routes.UnlockAfaController.onPageLoad(afaId).url
      }

      "have the correct unlock link text" in {
        elementText(linkSelector(2, 4, true)) mustBe s"Unlock draft application $afaId for $oldCompany"
      }

      "have the correct delete link" in {
        element(linkSelector(1, 5, true))
          .attr("href") mustBe controllers.routes.DeleteDraftController.onPageLoad(afaId).url
      }

      "have the correct delete link text" in {
        elementText(linkSelector(1, 5, true)) mustBe s"Delete draft application $afaId for $newCompany"
      }

      "have no link in delete column for unlock draft" in {
        element(linkSelector(2, 5)).hasAttr("href") mustBe false
      }

    }
  }
}
