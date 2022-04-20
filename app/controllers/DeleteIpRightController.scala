/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.DeleteIpRightFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.DeleteIpRightPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeleteIpRightView

import scala.concurrent.{ExecutionContext, Future}

class DeleteIpRightController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         getLock: LockAfaActionProvider,
                                         getData: AfaDraftDataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: DeleteIpRightFormProvider,
                                         validateIndex: IpRightsIndexActionFilterProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: DeleteIpRightView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId, index: Int): Action[AnyContent] = (identify
    andThen getLock(afaId)
    andThen getData(afaId)
    andThen requireData
    andThen validateIndex(index)) {
    implicit request =>

      Ok(view(form, mode, afaId, index))
  }

  def onSubmit(mode: Mode, afaId: AfaId, index: Int): Action[AnyContent] = (identify
    andThen getLock(afaId)
    andThen getData(afaId) andThen requireData
    andThen validateIndex(index)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, afaId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteIpRightPage(index), value))
          } yield Redirect(navigator.nextPage(DeleteIpRightPage(index), mode, updatedAnswers))
        }
      )
  }
}
