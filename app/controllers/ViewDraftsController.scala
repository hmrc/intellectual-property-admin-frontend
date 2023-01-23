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

class ViewDraftsController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  afaService: AfaService,
  lockService: LockService,
  view: ViewDraftsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    def draftLocked(draftId: AfaId)(lock: Lock): Boolean =
      lock._id == draftId && lock.userId != request.identifier

    val draftLockPairs = for {
      drafts <- afaService.draftList
      locks  <- lockService.lockList
    } yield drafts.map { userAnswers =>
      (userAnswers, locks.find(draftLocked(userAnswers.id)))
    }

    draftLockPairs.map { draftLockPairs =>
      val rows = draftLockPairs.map { case (userAnswers, lock) =>
        DraftRow.apply(userAnswers, lock.isDefined)
      }

      Ok(view(rows))
    }
  }
}
