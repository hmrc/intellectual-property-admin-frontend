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
import forms.IpRightsDescriptionFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IpRightsDescriptionPage, IpRightsTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsDescriptionView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsDescriptionController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  validateIndex: IpRightsIndexActionFilterProvider,
  formProvider: IpRightsDescriptionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IpRightsDescriptionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(implicit request: Request[_]): Form[String] = formProvider(request2Messages)

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(IpRightsDescriptionPage(index)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val ipRightType = request.userAnswers.get(IpRightsTypePage(index))

        Future.successful(Ok(view(preparedForm, mode, index, afaId, ipRightType)))
    }

  def onSubmit(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>
        val ipRightType = request.userAnswers.get(IpRightsTypePage(index))

        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, index, afaId, ipRightType))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(IpRightsDescriptionPage(index), value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IpRightsDescriptionPage(index), mode, updatedAnswers))
          )
    }
}
