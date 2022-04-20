/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject

import connectors.AfaConnector
import controllers.actions.IdentifierAction
import navigation.Navigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class CreateAfaIdController @Inject()(
                                       identify: IdentifierAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       navigator: Navigator,
                                       afaConnector: AfaConnector
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = identify.async {
    implicit request =>
      afaConnector.getNextAfaId() map {
        afaId =>

          Redirect(navigator.firstPage(afaId))
      }
  }
}
