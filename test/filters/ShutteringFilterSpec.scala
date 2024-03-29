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

package filters

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import views.html.ShutterPage

class ShutteringFilterSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues with Injecting {

  val requestHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "shuttered"             -> true,
      "shutter.urls.excluded" -> "/ping/ping"
    )
    .build()

  "a shuttering filter" - {

    "must shutter" - {

      "when the `shuttered` config property is true" in {

        val view = inject[ShutterPage]

        val result = route(app, FakeRequest(GET, controllers.routes.ViewDraftsController.onPageLoad().url)).value

        status(result) mustEqual SERVICE_UNAVAILABLE
        contentAsString(result) mustEqual view()(requestHeader).toString
      }
    }

    "must leave excluded URLs un-shuttered" in {

      val result = route(app, FakeRequest(GET, "/ping/ping")).value

      status(result) mustEqual OK
    }
  }
}
