/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.IdentifierAction
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SessionExpiredView

class SessionExpiredController @Inject()(
                                          identify: IdentifierAction,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: SessionExpiredView
                                        ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = identify { implicit request =>
    Ok(view()).withNewSession
  }
}
