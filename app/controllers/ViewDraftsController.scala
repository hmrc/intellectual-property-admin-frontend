/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import models.{AfaId, Lock}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AfaService, LockService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.DraftRow
import views.html.ViewDraftsView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ViewDraftsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       afaService: AfaService,
                                       lockService: LockService,
                                       view: ViewDraftsView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = identify.async {
    implicit request =>

      def draftLocked(draftId: AfaId)(lock: Lock): Boolean =
        lock._id == draftId && lock.userId != request.identifier

      val draftLockPairs = for {
        drafts <- afaService.draftList
        locks  <- lockService.lockList
      } yield drafts.map {
        userAnswers =>
          (userAnswers, locks.find(draftLocked(userAnswers.id)))
      }

      draftLockPairs.map {
        draftLockPairs =>

          val rows = draftLockPairs.map {
            case (userAnswers, lock) =>
              DraftRow.apply(userAnswers, lock.isDefined)
          }

          Ok(view(rows))
      }
  }
}
