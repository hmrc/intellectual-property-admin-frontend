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
import forms.UnlockAfaFormProvider
import models.requests.OptionalDataRequest
import models.{AfaId, NormalMode, UserAnswers}
import navigation.Navigator
import pages.{CompanyApplyingPage, UnlockAfaPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.LockService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.UnlockAfaView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnlockAfaController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     lockService: LockService,
                                     navigator: Navigator,
                                     identify: IdentifierAction,
                                     getData: AfaDraftDataRetrievalAction,
                                     formProvider: UnlockAfaFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: UnlockAfaView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(afaId: AfaId): Action[AnyContent] = (identify andThen getData(afaId)).async {
    implicit request =>
      getCompanyApplyingName {
        companyApplyingName =>

          lockService.getExistingLock(afaId).map {
            existingLock =>

              existingLock.map {
                lock =>
                  Ok(view(form, afaId, lock.name, companyApplyingName))
              }.getOrElse(throw new Exception("Expected AFA to be locked but it isn't"))
          }
      }
  }

  def onSubmit(afaId: AfaId): Action[AnyContent] = (identify andThen getData(afaId)).async {
    implicit request =>
      getCompanyApplyingName {
        companyApplyingName =>

          form.bindFromRequest().fold(
            formWithErrors =>

              lockService.getExistingLock(afaId).map {
                existingLock =>

                  existingLock.map {
                    lock =>
                      BadRequest(view(formWithErrors, afaId, lock.name, companyApplyingName))
                  }.getOrElse(throw new Exception("Expected AFA to be locked but it isn't"))
              },

            unlock => {

              val userAnswers = request.userAnswers.getOrElse(UserAnswers(afaId))

              for {
                updatedAnswers <- Future.fromTry(userAnswers.set(UnlockAfaPage, unlock))
                _ <- if (unlock) {
                  lockService.replaceLock(afaId)
                } else {
                  Future.successful(())
                }
              } yield Redirect(navigator.nextPage(UnlockAfaPage, NormalMode, updatedAnswers))
            }
          )
      }
  }

  private def getCompanyApplyingName(block: Option[String] => Future[Result])
                                    (implicit request: OptionalDataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.flatMap(_.get(CompanyApplyingPage)).map {
      company =>

        block(Some(company.acronym.getOrElse(company.name)))
    }.getOrElse(block(None))
  }
}
