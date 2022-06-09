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
import forms.IsRepresentativeContactUkBasedFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IsRepresentativeContactUkBasedPage, RepresentativeDetailsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsRepresentativeContactUkBasedView

import scala.concurrent.{ExecutionContext, Future}

class IsRepresentativeContactUkBasedController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          afaService: AfaService,
                                                          navigator: Navigator,
                                                          identify: IdentifierAction,
                                                          getLock: LockAfaActionProvider,
                                                          getData: AfaDraftDataRetrievalAction,
                                                          requireData: DataRequiredAction,
                                                          formProvider: IsRepresentativeContactUkBasedFormProvider,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          view: IsRepresentativeContactUkBasedView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        representativeContactName =>

          val form = formProvider(representativeContactName)

          val preparedForm = request.userAnswers.get(IsRepresentativeContactUkBasedPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, representativeContactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        representativeContactName =>

          val form = formProvider(representativeContactName)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, representativeContactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(IsRepresentativeContactUkBasedPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsRepresentativeContactUkBasedPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getRepresentativeContactName(block: String => Future[Result])
                                          (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(RepresentativeDetailsPage).map {
      representativeContact =>
        block(representativeContact.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))
  }
}
