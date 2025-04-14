/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import views.html.ErrorTemplate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest

class ErrorTemplateSpec extends AnyWordSpec with Matchers with GuiceOneAppPerTest {

  "ErrorTemplate view" should {

    "render the correct title, heading and message" in {
      val view     = app.injector.instanceOf[ErrorTemplate]
      val messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

      implicit val request = FakeRequest()
      implicit val msg     = messages

      val doc = Jsoup.parse(view("Error Page", "Oops!", "Something went wrong").body)

      doc.title()             shouldBe "Error Page - Protect intellectual property rights"
      doc.select("h1").text() shouldBe "Oops!"
      doc.select("p").text()  shouldBe "Something went wrong"
    }
  }
}
