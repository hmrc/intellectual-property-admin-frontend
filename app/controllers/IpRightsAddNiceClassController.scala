/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IpRightsAddNiceClassPage, IpRightsRemoveNiceClassPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionFunction, AnyContent, MessagesControllerComponents}
import queries.RemoveNiceClassQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.ReviewHelper
import viewmodels.ReviewRow
import views.html.IpRightsAddNiceClassView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsAddNiceClassController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                afaService: AfaService,
                                                navigator: Navigator,
                                                identify: IdentifierAction,
                                                getLock: LockAfaActionProvider,
                                                getData: AfaDraftDataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                validateIprIndex: IpRightsIndexActionFilterProvider,
                                                validateNiceClassIndex: NiceClassIndexActionFilterProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: IpRightsAddNiceClassView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, ipRightsIndex: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIprIndex(ipRightsIndex)) {
      implicit request =>

        val cyaHelper = new ReviewHelper(request.userAnswers)

        val reviewRows: Seq[ReviewRow] = cyaHelper.niceClassReviewRow(mode, ipRightsIndex).getOrElse(Seq.empty)
        val addNiceClassCall = navigator.nextPage(IpRightsAddNiceClassPage(ipRightsIndex), mode, request.userAnswers)

        Ok(view(mode, ipRightsIndex, afaId, reviewRows, addNiceClassCall.url))
    }


  private def validateIndices(iprIndex: Int, niceClassIndex: Int): ActionFunction[DataRequest, DataRequest] =
    validateIprIndex(iprIndex) andThen validateNiceClassIndex(iprIndex, niceClassIndex)

  def onDelete(mode: Mode, ipRightsIndex: Int, niceClassIndex: Int, afaId: AfaId): Action[AnyContent] = {
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndices(ipRightsIndex, niceClassIndex)).async {
      implicit request =>
        for {
          removedGoodsAnswers <- Future.fromTry(request.userAnswers.remove(RemoveNiceClassQuery(ipRightsIndex, niceClassIndex)))
          _ <- afaService.set(removedGoodsAnswers)
        } yield {
          Redirect(navigator.nextPage(IpRightsRemoveNiceClassPage(ipRightsIndex), mode, removedGoodsAnswers))
        }
    }
  }
}
