/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.ApplicantSecondaryLegalContactInternationalAddressFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, InternationalAddress, Mode}
import navigation.Navigator
import pages.{ApplicantSecondaryLegalContactInternationalAddressPage, WhoIsSecondaryLegalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApplicantSecondaryLegalContactInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class ApplicantSecondaryLegalContactInternationalAddressController @Inject()(
                                                                              override val messagesApi: MessagesApi,
                                                                              afaService: AfaService,
                                                                              navigator: Navigator,
                                                                              identify: IdentifierAction,
                                                                              getLock: LockAfaActionProvider,
                                                                              getData: AfaDraftDataRetrievalAction,
                                                                              requireData: DataRequiredAction,
                                                                              formProvider: ApplicantSecondaryLegalContactInternationalAddressFormProvider,
                                                                              val controllerComponents: MessagesControllerComponents,
                                                                              view: ApplicantSecondaryLegalContactInternationalAddressView
                                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[InternationalAddress] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getApplicantSecondaryLegalContactName {
        applicantSecondaryLegalContactName =>
          val preparedForm = request.userAnswers.get(ApplicantSecondaryLegalContactInternationalAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, applicantSecondaryLegalContactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getApplicantSecondaryLegalContactName {
        applicantSecondaryLegalContactName =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, applicantSecondaryLegalContactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ApplicantSecondaryLegalContactInternationalAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ApplicantSecondaryLegalContactInternationalAddressPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getApplicantSecondaryLegalContactName(block: String => Future[Result])
                                                   (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(WhoIsSecondaryLegalContactPage).map {
      secondaryLegalContactName =>
        block(secondaryLegalContactName.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }

}
