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
import forms.TechnicalContactInternationalAddressFormProvider

import javax.inject.Inject
import models.{AfaId, InternationalAddress, Mode}
import models.requests.DataRequest
import navigation.Navigator
import pages.{TechnicalContactInternationalAddressPage, WhoIsTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalContactInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class TechnicalContactInternationalAddressController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: TechnicalContactInternationalAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TechnicalContactInternationalAddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(implicit request: Request[_]): Form[InternationalAddress] = formProvider(request2Messages)

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getTechnicalContactName { contactName =>
        val preparedForm = request.userAnswers.get(TechnicalContactInternationalAddressPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getTechnicalContactName { contactName =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.set(TechnicalContactInternationalAddressPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(TechnicalContactInternationalAddressPage, mode, updatedAnswers))
          )
      }
    }

  private def getTechnicalContactName(
    block: String => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(WhoIsTechnicalContactPage)
      .map { technicalContact =>
        block(technicalContact.contactName)
      }
      .getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))
}
