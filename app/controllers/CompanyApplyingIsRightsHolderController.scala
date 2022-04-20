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
import forms.CompanyApplyingIsRightsHolderFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Enumerable, Mode}
import navigation.Navigator
import pages.{CompanyApplyingIsRightsHolderPage, CompanyApplyingPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CompanyApplyingIsRightsHolderView

import scala.concurrent.{ExecutionContext, Future}

class CompanyApplyingIsRightsHolderController @Inject()(
                                                         override val messagesApi: MessagesApi,
                                                         afaService: AfaService,
                                                         navigator: Navigator,
                                                         identify: IdentifierAction,
                                                         getLock: LockAfaActionProvider,
                                                         getData: AfaDraftDataRetrievalAction,
                                                         requireData: DataRequiredAction,
                                                         formProvider: CompanyApplyingIsRightsHolderFormProvider,
                                                         val controllerComponents: MessagesControllerComponents,
                                                         view: CompanyApplyingIsRightsHolderView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        companyName => {
          val preparedForm = request.userAnswers.get(CompanyApplyingIsRightsHolderPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, afaId, companyName)))
        }
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        companyName => {

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, companyName))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(CompanyApplyingIsRightsHolderPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(CompanyApplyingIsRightsHolderPage, mode, updatedAnswers))
            }
          )
        }
      }
  }


  private def getCompanyApplyingName(block: String => Future[Result])
                                    (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(CompanyApplyingPage).map {
      companyApplying =>

        block(companyApplying.acronym.getOrElse(companyApplying.name))
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }

}
