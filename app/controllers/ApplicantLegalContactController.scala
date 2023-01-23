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
import forms.ApplicantLegalContactFormProvider

import javax.inject.Inject
import models.{AfaId, ApplicantLegalContact, Mode}
import navigation.Navigator
import pages.ApplicantLegalContactPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApplicantLegalContactView
import utils.CommonHelpers

import scala.concurrent.{ExecutionContext, Future}

class ApplicantLegalContactController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ApplicantLegalContactFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ApplicantLegalContactView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form: Form[ApplicantLegalContact] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(ApplicantLegalContactPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      CommonHelpers.getApplicantName { name =>
        Ok(view(preparedForm, mode, afaId, name))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[_]) =>
            CommonHelpers.getApplicantName { name =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, name)))
            },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ApplicantLegalContactPage, value))
              _              <- afaService.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(ApplicantLegalContactPage, mode, updatedAnswers))
        )
    }
}
