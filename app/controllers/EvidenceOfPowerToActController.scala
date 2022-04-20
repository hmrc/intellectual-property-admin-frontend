/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.EvidenceOfPowerToActFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{EvidenceOfPowerToActPage, RepresentativeDetailsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.EvidenceOfPowerToActView

import scala.concurrent.{ExecutionContext, Future}

class EvidenceOfPowerToActController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                afaService: AfaService,
                                                navigator: Navigator,
                                                identify: IdentifierAction,
                                                getLock: LockAfaActionProvider,
                                                getData: AfaDraftDataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                formProvider: EvidenceOfPowerToActFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: EvidenceOfPowerToActView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        representativeContactName =>

          val preparedForm = request.userAnswers.get(EvidenceOfPowerToActPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, afaId, representativeContactName)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        representativeContactName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, representativeContactName))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(EvidenceOfPowerToActPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(EvidenceOfPowerToActPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getRepresentativeContactName(block: String => Future[Result])
                                          (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(RepresentativeDetailsPage).map {
      representativeContact =>
        block(representativeContact.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }
}

