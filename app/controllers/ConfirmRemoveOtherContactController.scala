/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import models.AfaId
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmRemoveOtherContactView

import javax.inject.Inject

class ConfirmRemoveOtherContactController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     identify: IdentifierAction,
                                                     getLock: LockAfaActionProvider,
                                                     getData: AfaDraftDataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: ConfirmRemoveOtherContactView) extends FrontendBaseController with I18nSupport {

  def onPageLoad(afaId: AfaId, contactToDelete: String): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>
      Ok(view(afaId, contactToDelete))
  }
}
