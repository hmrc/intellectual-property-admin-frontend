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

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import play.twirl.api.Html
import base.SpecBase
import models.{AfaId, UserAnswers}
import org.scalatest.Assertion
import scala.reflect.ClassTag

trait ViewSpecBase extends SpecBase {

  val afaId: AfaId = userAnswersId

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String): Assertion =
    assertEqualsValue(doc, cssSelector, messages(expectedMessageKey))

  def elementText(selector: String)(implicit document: Document): String =
    element(selector).text()

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }

  def elementNotExist(selector: String)(implicit document: Document): Unit = {
    val elements = document.select(selector)
    if (elements.size != 0) {
      fail(s"Elements incorrectly exist with this selector '$selector'")
    }
  }

  def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    // <p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def assertPageTitleEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1
    headers.first.text.replaceAll("\u00a0", " ") mustBe messages(expectedMessageKey, args: _*).replaceAll("&nbsp;", " ")
  }

  def assertPageTitleWithAfaIdEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    import models.Formatter.HtmlFormattedOps

    val headers = doc.getElementsByTag("h1")

    headers.size mustBe 1
    headers.first.text
      .replaceAll("\u00a0", " ") mustBe s"${messages("site.afaId")} ${afaId.format} ${messages(expectedMessageKey, args: _*)
        .replaceAll("&nbsp;", " ")}"
  }

  def assertAfaIdHeaderReferencePresent(doc: Document): Assertion = {
    val afaIdHeadingReference = doc.getElementById("afaid-header")

    afaIdHeadingReference.text
      .replaceAll("\u00a0", " ") mustBe s"${messages("site.afaId")} $afaId"
  }

  def assertContainsText(doc: Document, text: String): Assertion =
    assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def assertContainsMessages(doc: Document, expectedMessageKeys: String*): Unit =
    for (key <- expectedMessageKeys) assertContainsText(doc, messages(key))

  def assertRenderedById(doc: Document, id: String): Assertion =
    assert(doc.getElementById(id) != null, "\n\nElement " + id + " was not rendered on the page.\n")

  def assertNotRenderedById(doc: Document, id: String): Assertion =
    assert(doc.getElementById(id) == null, "\n\nElement " + id + " was rendered on the page.\n")

  def assertRenderedByClass(doc: Document, className: String): Assertion =
    assert(doc.getElementsByClass(className) != null, "\n\nElement " + className + " was not rendered on the page.\n")

  def assertNotRenderedByClass(doc: Document, className: String): Assertion =
    assert(doc.getElementsByClass(className).isEmpty, "\n\nElement " + className + " was rendered on the page.\n")

  def assertRenderedByCssSelector(doc: Document, cssSelector: String): Assertion =
    assert(!doc.select(cssSelector).isEmpty, "Element " + cssSelector + " was not rendered on the page.")

  def assertNotRenderedByCssSelector(doc: Document, cssSelector: String): Assertion =
    assert(doc.select(cssSelector).isEmpty, "\n\nElement " + cssSelector + " was rendered on the page.\n")

  def assertContainsLabel(
    doc: Document,
    forElement: String,
    expectedText: String,
    expectedHintText: Option[String] = None
  ): Any = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    val label  = labels.first
    assert(label.text().contains(expectedText), s"\n\nLabel for $forElement was not $expectedText")

    if (expectedHintText.isDefined) {
      assert(
        label.getElementsByClass("form-hint").first.text == expectedHintText.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintText"
      )
    }
  }

  def assertElementHasClass(doc: Document, id: String, expectedClass: String): Assertion =
    assert(doc.getElementById(id).hasClass(expectedClass), s"\n\nElement $id does not have class $expectedClass")

  def assertContainsRadioButton(
    doc: Document,
    id: String,
    name: String,
    value: String,
    isChecked: Boolean
  ): Assertion = {
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!radio.hasAttr("checked") && radio.attr("checked") != "checked", s"\n\nElement $id is checked")
    }
  }

  def assertContainsRadioOption(
    doc: Document,
    id: String,
    name: String,
    value: String,
    selectionType: String,
    isChecked: Boolean
  ): Assertion = {
    assertRenderedById(doc, id)
    val optionItem = doc.getElementById(id)
    assert(optionItem.attr("type") == selectionType, s"\n\nElement $id is not of the type $selectionType")
    assert(optionItem.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(optionItem.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(optionItem.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!optionItem.hasAttr("checked") && optionItem.attr("checked") != "checked", s"\n\nElement $id is checked")
    }
  }

  def injectInstanceOf[A](data: Option[UserAnswers] = None)(implicit tag: ClassTag[A]): A = {
    val application = applicationBuilder(data).build()
    val view        = application.injector.instanceOf[A]
    application.stop()
    view
  }

}
