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
import forms.IpRightsSupplementaryProtectionCertificateTypeFormProvider

import javax.inject.Inject
import models.{AfaId, Enumerable, Mode}
import navigation.Navigator
import pages.IpRightsSupplementaryProtectionCertificateTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsSupplementaryProtectionCertificateTypeView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsSupplementaryProtectionCertificateTypeController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  validateIndex: IpRightsIndexActionFilterProvider,
  formProvider: IpRightsSupplementaryProtectionCertificateTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IpRightsSupplementaryProtectionCertificateTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Enumerable.Implicits {

  private def form = formProvider()

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)) {
      implicit request =>
        val preparedForm = request.userAnswers.get(IpRightsSupplementaryProtectionCertificateTypePage(index)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, index, afaId))
    }

  def onSubmit(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, mode, index, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future
                    .fromTry(request.userAnswers.set(IpRightsSupplementaryProtectionCertificateTypePage(index), value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(
                navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(index), mode, updatedAnswers)
              )
          )
    }
}
