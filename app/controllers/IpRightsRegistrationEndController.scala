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
import forms.IpRightsRegistrationEndFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, IpRightsType, Mode}
import navigation.Navigator
import pages.{IpRightsRegistrationEndPage, IpRightsTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsRegistrationEndView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsRegistrationEndController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   afaService: AfaService,
                                                   navigator: Navigator,
                                                   identify: IdentifierAction,
                                                   getLock: LockAfaActionProvider,
                                                   getData: AfaDraftDataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   validateIndex: IpRightsIndexActionFilterProvider,
                                                   formProvider: IpRightsRegistrationEndFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: IpRightsRegistrationEndView
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {

      implicit request =>
        getIpRightType(index) {
          ipRightsType =>

            val form = formProvider(args = Seq(ipRightsType))

            val preparedForm = request.userAnswers.get(IpRightsRegistrationEndPage(index)) match {
              case None => form
              case Some(value) => form.fill(value)
            }

            Future.successful(Ok(view(preparedForm, mode, index, afaId, ipRightsType)))
        }
    }

  def onSubmit(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {

      implicit request =>
        getIpRightType(index) {
          ipRightsType =>

            val form = formProvider(args = Seq(ipRightsType))

            form.bindFromRequest().fold(
              (formWithErrors: Form[_]) =>
                Future.successful(BadRequest(view(formWithErrors, mode, index, afaId, ipRightsType))),

              value => {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(IpRightsRegistrationEndPage(index), value))
                  _ <- afaService.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(IpRightsRegistrationEndPage(index), mode, updatedAnswers))
              }
            )
        }
    }

  private def getIpRightType(index: Int)(block: String => Future[Result])
                            (implicit request: DataRequest[AnyContent], messages: Messages): Future[Result] = {

    request.userAnswers.get(IpRightsTypePage(index)).map {
      rightsType: IpRightsType =>

        block(messages(s"ipRightsType.${rightsType.toString}").toLowerCase)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad)))
  }
}
