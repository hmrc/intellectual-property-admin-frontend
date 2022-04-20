/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package filters

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import views.html.ShutterPage

class ShutteringFilterSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues with Injecting {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "shuttered" -> true,
      "shutter.urls.excluded" -> "/ping/ping"
    )
    .build()

  "a shuttering filter" - {

    "must shutter" - {

      "when the `shuttered` config property is true" in {

        val view = inject[ShutterPage]

        val result = route(app, FakeRequest(GET, controllers.routes.ViewDraftsController.onPageLoad().url)).value

        status(result) mustEqual SERVICE_UNAVAILABLE
        contentAsString(result) mustEqual view().toString
      }
    }

    "must leave excluded URLs un-shuttered" in {

      val result = route(app, FakeRequest(GET, "/ping/ping")).value

      status(result) mustEqual OK
    }
  }
}
