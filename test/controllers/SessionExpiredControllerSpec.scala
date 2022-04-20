/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SessionExpiredView

class SessionExpiredControllerSpec extends SpecBase {

  "SessionExpired Controller" must {

    val application = applicationBuilder(userAnswers = None).build()

    val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad().url)

    val result = route(application, request).value

    val view = application.injector.instanceOf[SessionExpiredView]

    "return OK and the correct view for a GET" in {

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString
    }

    "session should be empty" in {
      session(result) mustBe empty

      application.stop()
    }
  }
}

