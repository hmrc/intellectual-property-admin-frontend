/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import views.behaviours.ViewBehaviours
import views.html.UnauthorisedView

class UnauthorisedViewSpec extends ViewBehaviours {

  "Unauthorised view" must {

    val view = injectInstanceOf[UnauthorisedView]()

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView, "unauthorised", afaIdInHeader = false)
  }
}
