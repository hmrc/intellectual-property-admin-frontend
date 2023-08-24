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
import forms.SecondaryTechnicalContactInternationalAddressFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, InternationalAddress, Mode}
import navigation.Navigator
import pages.{SecondaryTechnicalContactInternationalAddressPage, WhoIsSecondaryTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SecondaryTechnicalContactInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class SecondaryTechnicalContactInternationalAddressController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: SecondaryTechnicalContactInternationalAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SecondaryTechnicalContactInternationalAddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(implicit request: Request[_]): Form[InternationalAddress] = formProvider(request2Messages)

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getSecondaryTechnicalContactName { secondaryTechnicallContactName =>
        val preparedForm = request.userAnswers.get(SecondaryTechnicalContactInternationalAddressPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, secondaryTechnicallContactName, afaId)))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getSecondaryTechnicalContactName { secondaryTechnicalContactName =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, secondaryTechnicalContactName, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.set(SecondaryTechnicalContactInternationalAddressPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(
                navigator.nextPage(SecondaryTechnicalContactInternationalAddressPage, mode, updatedAnswers)
              )
          )
      }
    }

  private def getSecondaryTechnicalContactName(
    block: String => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(WhoIsSecondaryTechnicalContactPage)
      .map { secondaryTechnicalContactName =>
        block(secondaryTechnicalContactName.contactName)
      }
      .getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))

}
