/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.{AfaDraftDataRetrievalAction, DataRequiredAction, IdentifierAction, LockAfaActionProvider}
import models.AfaId
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmedRemovedContactView

import javax.inject.Inject

class ConfirmedRemovedContactController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   identify: IdentifierAction,
                                                   getLock: LockAfaActionProvider,
                                                   getData: AfaDraftDataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ConfirmedRemovedContactView) extends FrontendBaseController with I18nSupport {


  def onPageLoad(afaId: AfaId, contactToRemove: String): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
      implicit request =>

        Ok(view(afaId, contactToRemove))
  }
}
