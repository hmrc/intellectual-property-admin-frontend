/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.IsTechnicalContactUkBasedFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IsTechnicalContactUkBasedPage, WhoIsTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsTechnicalContactUkBasedView

import scala.concurrent.{ExecutionContext, Future}

class IsTechnicalContactUkBasedController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     afaService: AfaService,
                                                     navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getLock: LockAfaActionProvider,
                                                     getData: AfaDraftDataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     formProvider: IsTechnicalContactUkBasedFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: IsTechnicalContactUkBasedView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getInfringementContactName {
        infringementContactName =>

          val form = formProvider(infringementContactName)

          val preparedForm = request.userAnswers.get(IsTechnicalContactUkBasedPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, infringementContactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getInfringementContactName {
        infringementContactName =>

          val form = formProvider(infringementContactName)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, infringementContactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(IsTechnicalContactUkBasedPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsTechnicalContactUkBasedPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getInfringementContactName(block: String => Future[Result])
                                        (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(WhoIsTechnicalContactPage).map {
      infringementContact =>

        block(infringementContact.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }
}
