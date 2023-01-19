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
import forms.DeleteDraftFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, NormalMode}
import navigation.Navigator
import pages.{CompanyApplyingPage, DeleteDraftPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{AfaService, LockService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeleteDraftView

import scala.concurrent.{ExecutionContext, Future}

class DeleteDraftController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  lockService: LockService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DeleteDraftFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DeleteDraftView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form = formProvider()

  def onPageLoad(afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getCompanyApplyingName { companyApplyingName =>
        Future.successful(Ok(view(form, afaId, companyApplyingName)))
      }
    }

  def onSubmit(afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getCompanyApplyingName { companyApplyingName =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, afaId, companyApplyingName))),
            deleteDraft =>
              if (deleteDraft) {

                for {
                  _              <- afaService.removeDraft(afaId)
                  _              <- lockService.removeLock(afaId)
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteDraftPage, deleteDraft))
                } yield Redirect(navigator.nextPage(DeleteDraftPage, NormalMode, updatedAnswers))

              } else {

                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteDraftPage, deleteDraft))
                } yield Redirect(navigator.nextPage(DeleteDraftPage, NormalMode, updatedAnswers))
              }
          )
      }
    }

  private def getCompanyApplyingName(
    block: String => Future[Result]
  )(implicit request: DataRequest[AnyContent], messages: Messages): Future[Result] =
    request.userAnswers
      .get(CompanyApplyingPage)
      .map { company =>
        block(company.acronym.getOrElse(company.name))
      }
      .getOrElse(block(messages("companyApplying.unknown")))
}
