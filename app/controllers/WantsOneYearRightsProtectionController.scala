/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.WantsOneYearRightsProtectionFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.WantsOneYearRightsProtectionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.WantsOneYearRightsProtectionView

import scala.concurrent.{ExecutionContext, Future}

class WantsOneYearRightsProtectionController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        afaService: AfaService,
                                                        navigator: Navigator,
                                                        identify: IdentifierAction,
                                                        getLock: LockAfaActionProvider,
                                                        getData: AfaDraftDataRetrievalAction,
                                                        requireData: DataRequiredAction,
                                                        formProvider: WantsOneYearRightsProtectionFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: WantsOneYearRightsProtectionView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WantsOneYearRightsProtectionPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WantsOneYearRightsProtectionPage, value))
            _              <- afaService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WantsOneYearRightsProtectionPage, mode, updatedAnswers))
        }
      )
  }
}
