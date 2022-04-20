/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.WhoIsSecondaryTechnicalContactFormProvider

import javax.inject.Inject
import models.{AfaId, Mode, TechnicalContact}
import navigation.Navigator
import pages.WhoIsSecondaryTechnicalContactPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CommonHelpers
import views.html.WhoIsSecondaryTechnicalContactView

import scala.concurrent.{ExecutionContext, Future}

class WhoIsSecondaryTechnicalContactController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          afaService: AfaService,
                                                          navigator: Navigator,
                                                          identify: IdentifierAction,
                                                          getLock: LockAfaActionProvider,
                                                          getData: AfaDraftDataRetrievalAction,
                                                          requireData: DataRequiredAction,
                                                          formProvider: WhoIsSecondaryTechnicalContactFormProvider,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          view: WhoIsSecondaryTechnicalContactView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[TechnicalContact] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhoIsSecondaryTechnicalContactPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      CommonHelpers.getApplicantName {
        name =>
          Ok(view(preparedForm, mode, afaId, name))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      CommonHelpers.getApplicantName {
        name =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, name))),

            value => {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(WhoIsSecondaryTechnicalContactPage, value))
                  _ <- afaService.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(WhoIsSecondaryTechnicalContactPage, mode, updatedAnswers))
            }
          )

      }

  }
}
