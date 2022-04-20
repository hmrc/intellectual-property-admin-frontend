/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.WhoIsSecondaryLegalContactFormProvider

import javax.inject.Inject
import models.{AfaId, Mode, WhoIsSecondaryLegalContact}
import navigation.Navigator
import pages.WhoIsSecondaryLegalContactPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CommonHelpers
import views.html.WhoIsSecondaryLegalContactView

import scala.concurrent.{ExecutionContext, Future}

class WhoIsSecondaryLegalContactController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      afaService: AfaService,
                                                      navigator: Navigator,
                                                      identify: IdentifierAction,
                                                      getLock: LockAfaActionProvider,
                                                      getData: AfaDraftDataRetrievalAction,
                                                      requireData: DataRequiredAction,
                                                      formProvider: WhoIsSecondaryLegalContactFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: WhoIsSecondaryLegalContactView
                                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[WhoIsSecondaryLegalContact] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhoIsSecondaryLegalContactPage) match {
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
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          CommonHelpers.getApplicantName {
            name =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, name)))
          },

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhoIsSecondaryLegalContactPage, value))
            _ <- afaService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhoIsSecondaryLegalContactPage, mode, updatedAnswers))
        }
      )
  }
}
