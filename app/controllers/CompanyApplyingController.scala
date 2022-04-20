/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.CompanyApplyingFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.CompanyApplyingPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CompanyApplyingView

import scala.concurrent.{ExecutionContext, Future}

class CompanyApplyingController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           afaService: AfaService,
                                           navigator: Navigator,
                                           identify: IdentifierAction,
                                           getLock: LockAfaActionProvider,
                                           getData: AfaDraftDataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           formProvider: CompanyApplyingFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: CompanyApplyingView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CompanyApplyingPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, afaId))
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, afaId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CompanyApplyingPage, value))
            _              <- afaService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CompanyApplyingPage, mode, updatedAnswers))
        }
      )
  }
}
