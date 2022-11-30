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

import javax.inject.Inject
import controllers.actions._
import forms.IsCompanyApplyingUkBasedFormProvider
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.IsCompanyApplyingUkBasedPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.CompanyApplyingNameQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsCompanyApplyingUkBasedView

import scala.concurrent.{ExecutionContext, Future}

class IsCompanyApplyingUkBasedController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IsCompanyApplyingUkBasedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsCompanyApplyingUkBasedView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getCompanyApplyingName { companyApplyingName =>
        val form = formProvider(companyApplyingName)

        val preparedForm = request.userAnswers.get(IsCompanyApplyingUkBasedPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, companyApplyingName, afaId)))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getCompanyApplyingName { companyApplyingName =>
        val form = formProvider(companyApplyingName)

        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, companyApplyingName, afaId))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(IsCompanyApplyingUkBasedPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsCompanyApplyingUkBasedPage, mode, updatedAnswers))
          )
      }
    }

  private def getCompanyApplyingName(
    block: String => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(CompanyApplyingNameQuery)
      .map { companyApplyingName =>
        block(companyApplyingName)
      }
      .getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))

}
