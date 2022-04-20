/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.DeleteDraftFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, NormalMode}
import navigation.Navigator
import pages.{CompanyApplyingPage, DeleteDraftPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{AfaService, LockService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeleteDraftView

import scala.concurrent.{ExecutionContext, Future}

class DeleteDraftController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       afaService: AfaService,
                                       lockService: LockService,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getLock: LockAfaActionProvider,
                                       getData: AfaDraftDataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: DeleteDraftFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeleteDraftView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        companyApplyingName =>

          Future.successful(Ok(view(form, afaId, companyApplyingName)))
      }
  }

  def onSubmit(afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        companyApplyingName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, afaId, companyApplyingName))),

            deleteDraft =>
              if (deleteDraft) {

                for {
                  _              <- afaService.removeDraft(afaId)
                  _              <- lockService.removeLock(afaId)
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteDraftPage, deleteDraft))
                } yield Redirect(navigator.nextPage(DeleteDraftPage, NormalMode, updatedAnswers))

              } else {

                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteDraftPage, deleteDraft))
                } yield Redirect(navigator.nextPage(DeleteDraftPage, NormalMode, updatedAnswers))
              }
          )
      }
  }

  private def getCompanyApplyingName(block: String => Future[Result])
                                    (implicit request: DataRequest[AnyContent], messages: Messages): Future[Result] = {

    request.userAnswers.get(CompanyApplyingPage).map {
      company =>

        block(company.acronym.getOrElse(company.name))
    }.getOrElse(block(messages("companyApplying.unknown")))
  }
}
