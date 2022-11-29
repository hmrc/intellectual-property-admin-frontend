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
import javax.inject.Inject
import models.AfaId
import pages.{AddAnotherLegalContactPage, AddAnotherTechnicalContactPage, ApplicantSecondaryLegalContactInternationalAddressPage, ApplicantSecondaryLegalContactUkAddressPage, IsApplicantSecondaryLegalContactUkBasedPage, IsSecondaryTechnicalContactUkBasedPage, SecondaryTechnicalContactInternationalAddressPage, SecondaryTechnicalContactUkAddressPage, WhoIsSecondaryLegalContactPage, WhoIsSecondaryTechnicalContactPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class RemoveOtherContactController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  afaService: AfaService,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onDelete(afaId: AfaId, contactToRemove: String): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async { implicit request =>
      contactToRemove match {
        case "legal" =>
          for {
            updatedAnswers  <- Future.fromTry(request.userAnswers.set(AddAnotherLegalContactPage, false))
            updatedAnswers1 <- Future.fromTry(updatedAnswers.remove(WhoIsSecondaryLegalContactPage))
            updatedAnswers2 <- Future.fromTry(updatedAnswers1.remove(IsApplicantSecondaryLegalContactUkBasedPage))
            updatedAnswers3 <- Future.fromTry(updatedAnswers2.remove(ApplicantSecondaryLegalContactUkAddressPage))
            updatedAnswers4 <-
              Future.fromTry(updatedAnswers3.remove(ApplicantSecondaryLegalContactInternationalAddressPage))
            _               <- afaService.set(updatedAnswers4)
          } yield Redirect(routes.ConfirmedRemovedContactController.onPageLoad(afaId, contactToRemove))

        case "technical" =>
          for {
            updatedAnswers  <- Future.fromTry(request.userAnswers.set(AddAnotherTechnicalContactPage, false))
            updatedAnswers1 <- Future.fromTry(updatedAnswers.remove(WhoIsSecondaryTechnicalContactPage))
            updatedAnswers2 <- Future.fromTry(updatedAnswers1.remove(IsSecondaryTechnicalContactUkBasedPage))
            updatedAnswers3 <- Future.fromTry(updatedAnswers2.remove(SecondaryTechnicalContactUkAddressPage))
            updatedAnswers4 <- Future.fromTry(updatedAnswers3.remove(SecondaryTechnicalContactInternationalAddressPage))
            _               <- afaService.set(updatedAnswers4)
          } yield Redirect(routes.ConfirmedRemovedContactController.onPageLoad(afaId, contactToRemove))

        case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad))
      }
    }
}
