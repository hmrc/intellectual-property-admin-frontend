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

import models.NormalMode
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.Call
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.{AnswerSection, IpRightAnswerRowWithUrl}
import views.behaviours.ViewBehaviours
import views.html.CheckIprDetailsView

class CheckIprDetailsViewSpec extends ViewBehaviours {
  val emptyCall = Call("Foo", "Bar", "Baz")

  val singleLine: AnswerSection =
    AnswerSection(None, Seq(IpRightAnswerRowWithUrl("TestLabel", Html("TestHtml"), "TestUrl", 1)))

  val doubleLine: AnswerSection = AnswerSection(
    None,
    Seq(
      IpRightAnswerRowWithUrl("TestLabel", Html("TestHtml"), "TestUrl", 1),
      IpRightAnswerRowWithUrl("TestLabel2", Html("TestHtml2"), "TestUrl2", 2)
    )
  )

  val view = injectInstanceOf[CheckIprDetailsView]()

  def applyView(answers: AnswerSection): HtmlFormat.Appendable =
    view.apply(NormalMode, afaId, 0, answers, None, emptyCall)(messages)

  behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(singleLine), "checkIprDetails")

  "display the correct single answer line" in {
    implicit val doc: Document = asDocument(applyView(singleLine))

    val row   = doc.getElementsByClass("govuk-summary-list__row")
    val label = row.select("dt")
    label.text shouldBe "TestLabel"

    val body = row.select("dd.govuk-summary-list__value")
    body.html shouldBe "TestHtml"

    val url = row.select("dd > a")
    url.attr("href") shouldBe "TestUrl"
  }

  "display the correct multiple lines" in {
    implicit val doc: Document = asDocument(applyView(doubleLine))

    val row   = doc.select("div:eq(0).govuk-summary-list__row")
    val label = row.select("dt")
    label.text shouldBe "TestLabel"

    val body = row.select("dd.govuk-summary-list__value")
    body.html shouldBe "TestHtml"

    val url = row.select("dd > a")
    url.attr("href") shouldBe "TestUrl"

    val row2   = doc.select("div:eq(1).govuk-summary-list__row")
    val label2 = row2.select("dt")
    label2.text shouldBe "TestLabel2"

    val body2 = row2.select("dd.govuk-summary-list__value")
    body2.html shouldBe "TestHtml2"

    val url2 = row2.select("dd > a")
    url2.attr("href") shouldBe "TestUrl2"
  }

  behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(singleLine))

}
