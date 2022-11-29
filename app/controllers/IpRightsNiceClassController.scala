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
import forms.IpRightsNiceClassFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode, NiceClassId}
import navigation.Navigator
import pages.IpRightsNiceClassPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import queries.NiceClassIdsQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsNiceClassView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsNiceClassController @Inject() (
  override val messagesApi: MessagesApi,
  afaService: AfaService,
  navigator: Navigator,
  identify: IdentifierAction,
  getLock: LockAfaActionProvider,
  getData: AfaDraftDataRetrievalAction,
  requireData: DataRequiredAction,
  validateIprIndex: IpRightsIndexActionFilterProvider,
  validateNiceClassIndex: NiceClassIndexActionFilterProvider,
  formProvider: IpRightsNiceClassFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IpRightsNiceClassView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def validateIndices(iprIndex: Int, goodsIndex: Int): ActionFunction[DataRequest, DataRequest] =
    validateIprIndex(iprIndex) andThen validateNiceClassIndex(iprIndex, goodsIndex)

  def onPageLoad(mode: Mode, iprIndex: Int, niceClassIndex: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndices(
      iprIndex,
      niceClassIndex
    )).async { implicit request =>
      getOtherExistingNiceClasses(iprIndex, niceClassIndex) { existingClasses =>
        val form = formProvider(existingClasses)

        val preparedForm = request.userAnswers.get(IpRightsNiceClassPage(iprIndex, niceClassIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, mode, iprIndex, niceClassIndex, afaId)))
      }
    }

  def onSubmit(mode: Mode, iprIndex: Int, niceClassIndex: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndices(
      iprIndex,
      niceClassIndex
    )).async { implicit request =>
      getOtherExistingNiceClasses(iprIndex, niceClassIndex) { existingClasses =>
        val form = formProvider(existingClasses)

        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, iprIndex, niceClassIndex, afaId))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.set(IpRightsNiceClassPage(iprIndex, niceClassIndex), value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(
                navigator.nextPage(IpRightsNiceClassPage(iprIndex, niceClassIndex), mode, updatedAnswers)
              )
          )
      }
    }

  private def getOtherExistingNiceClasses(index: Int, niceClassIndex: Int)(
    block: Seq[NiceClassId] => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(NiceClassIdsQuery(index))
      .map { existingClasses =>
        val otherClasses = existingClasses
          .lift(niceClassIndex)
          .map { currentNiceClass =>
            existingClasses.filter(_ != currentNiceClass)
          }
          .getOrElse(existingClasses)

        block(otherClasses)
      }
      .getOrElse(block(Seq.empty))
}
