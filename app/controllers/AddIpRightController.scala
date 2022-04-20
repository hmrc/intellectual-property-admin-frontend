/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._

import javax.inject.Inject
import models.{AfaId, CheckMode, Mode}
import navigation.Navigator
import pages.{AddIpRightPage, RemoveIprPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.RemoveIprQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.ReviewHelper
import views.html.AddIpRightView

import scala.concurrent.{ExecutionContext, Future}

class AddIpRightController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      afaService: AfaService,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getLock: LockAfaActionProvider,
                                      getData: AfaDraftDataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      validateIndex: IpRightsIndexActionFilterProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: AddIpRightView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val reviewRows = new ReviewHelper(request.userAnswers).iprReviewRow(mode)

      val numberOfIPRights: Int = reviewRows.fold(_ => 0, _.size)

      val addIpRightCall = navigator.nextPage(AddIpRightPage, mode, request.userAnswers)

      val nextPage = if (mode == CheckMode) {
        routes.CheckYourAnswersController.onPageLoad(afaId)
      } else {
        routes.AdditionalInfoProvidedController.onPageLoad(mode, afaId)
      }

      Ok(view(mode, afaId, reviewRows, addIpRightCall.url, numberOfIPRights, nextPage))
  }

  def onDelete(mode: Mode, afaId: AfaId, index: Int): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>

        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.remove(RemoveIprQuery(index)))
          _ <- afaService.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(RemoveIprPage, mode, updatedAnswers))
    }
}
