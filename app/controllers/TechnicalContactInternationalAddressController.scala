/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.TechnicalContactInternationalAddressFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import models.requests.DataRequest
import navigation.Navigator
import pages.{TechnicalContactInternationalAddressPage, WhoIsTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalContactInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class TechnicalContactInternationalAddressController @Inject()(
                                                                override val messagesApi: MessagesApi,
                                                                afaService: AfaService,
                                                                navigator: Navigator,
                                                                identify: IdentifierAction,
                                                                getLock: LockAfaActionProvider,
                                                                getData: AfaDraftDataRetrievalAction,
                                                                requireData: DataRequiredAction,
                                                                formProvider: TechnicalContactInternationalAddressFormProvider,
                                                                val controllerComponents: MessagesControllerComponents,
                                                                view: TechnicalContactInternationalAddressView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getTechnicalContactName {
        contactName =>

          val preparedForm = request.userAnswers.get(TechnicalContactInternationalAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getTechnicalContactName {
        contactName =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(TechnicalContactInternationalAddressPage, value))
                _              <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(TechnicalContactInternationalAddressPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getTechnicalContactName(block: String => Future[Result])
                                     (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(WhoIsTechnicalContactPage).map {
      technicalContact =>
        block(technicalContact.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }
}
