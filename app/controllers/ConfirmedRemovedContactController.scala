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

import controllers.actions.{AfaDraftDataRetrievalAction, DataRequiredAction, IdentifierAction, LockAfaActionProvider}
import models.AfaId
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmedRemovedContactView

import javax.inject.Inject

class ConfirmedRemovedContactController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmedRemovedContactView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(afaId: AfaId, contactToRemove: String): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) { implicit request =>
      Ok(view(afaId, contactToRemove))
    }
}
