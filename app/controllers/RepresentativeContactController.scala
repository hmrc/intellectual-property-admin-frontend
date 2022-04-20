/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.RepresentativeContactFormProvider

import javax.inject.Inject
import models.RepresentativeDetails
import models.{AfaId, Mode}
import navigation.Navigator
import pages.RepresentativeDetailsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.RepresentativeContactView
import utils.CommonHelpers

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeContactController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 afaService: AfaService,
                                                 navigator: Navigator,
                                                 identify: IdentifierAction,
                                                 getLock: LockAfaActionProvider,
                                                 getData: AfaDraftDataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: RepresentativeContactFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: RepresentativeContactView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[RepresentativeDetails] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData) {
    implicit request =>

      val preparedForm: Form[RepresentativeDetails] = request.userAnswers.get(RepresentativeDetailsPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(RepresentativeDetailsPage, value))
            _              <- afaService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(RepresentativeDetailsPage, mode, updatedAnswers))
        }
      )
  }
}
