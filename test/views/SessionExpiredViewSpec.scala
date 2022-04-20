/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import views.behaviours.ViewBehaviours
import views.html.SessionExpiredView

class SessionExpiredViewSpec extends ViewBehaviours {

  "Session Expired view" must {

    val view = injectInstanceOf[SessionExpiredView]()

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView, "session_expired", afaIdInHeader = false)
    behave like pageWithGuidance(applyView, "session_expired", Seq("guidance.one", "guidance.two"))
  }

}
