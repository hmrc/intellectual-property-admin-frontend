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
import forms.ApplicantLegalContactUkAddressFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode, UkAddress}
import navigation.Navigator
import pages.{ApplicantSecondaryLegalContactUkAddressPage, WhoIsSecondaryLegalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApplicantSecondaryLegalContactUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class ApplicantSecondaryLegalContactUkAddressController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ApplicantLegalContactUkAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ApplicantSecondaryLegalContactUkAddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form: Form[UkAddress] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getApplicantSecondaryLegalContactName { contactName =>
        val preparedForm = request.userAnswers.get(ApplicantSecondaryLegalContactUkAddressPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
    }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      getApplicantSecondaryLegalContactName { contactName =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.set(ApplicantSecondaryLegalContactUkAddressPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ApplicantSecondaryLegalContactUkAddressPage, mode, updatedAnswers))
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
