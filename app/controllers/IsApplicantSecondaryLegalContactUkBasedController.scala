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
import forms.IsApplicantSecondaryLegalContactUkBasedFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IsApplicantSecondaryLegalContactUkBasedPage, WhoIsSecondaryLegalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsApplicantSecondaryLegalContactUkBasedView

import scala.concurrent.{ExecutionContext, Future}

class IsApplicantSecondaryLegalContactUkBasedController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IsApplicantSecondaryLegalContactUkBasedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsApplicantSecondaryLegalContactUkBasedView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getApplicantSecondaryLegalContactName { applicantSecondaryLegalContactName =>
        val form: Form[Boolean] = formProvider(applicantSecondaryLegalContactName)

        val preparedForm = request.userAnswers.get(IsApplicantSecondaryLegalContactUkBasedPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, applicantSecondaryLegalContactName, afaId)))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getApplicantSecondaryLegalContactName { applicantSecondaryLegalContactName =>
        val form = formProvider(applicantSecondaryLegalContactName)

        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, applicantSecondaryLegalContactName, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.set(IsApplicantSecondaryLegalContactUkBasedPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsApplicantSecondaryLegalContactUkBasedPage, mode, updatedAnswers))
          )
      }
    }

  private def getApplicantSecondaryLegalContactName(
    block: String => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(WhoIsSecondaryLegalContactPage)
      .map { secondaryLegalContactName =>
        block(secondaryLegalContactName.contactName)
      }
      .getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))
}
