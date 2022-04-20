/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject
import controllers.actions._
import forms.ApplicantLegalContactInternationalAddressFormProvider
import models.requests.DataRequest
import models.{AfaId, InternationalAddress, Mode}
import navigation.Navigator
import pages.ApplicantLegalContactInternationalAddressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.ApplicantLegalContactNameQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApplicantLegalContactInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class ApplicantLegalContactInternationalAddressController @Inject()(
                                                                     override val messagesApi: MessagesApi,
                                                                     afaService: AfaService,
                                                                     navigator: Navigator,
                                                                     identify: IdentifierAction,
                                                                     getLock: LockAfaActionProvider,
                                                                     getData: AfaDraftDataRetrievalAction,
                                                                     requireData: DataRequiredAction,
                                                                     formProvider: ApplicantLegalContactInternationalAddressFormProvider,
                                                                     val controllerComponents: MessagesControllerComponents,
                                                                     view: ApplicantLegalContactInternationalAddressView
                                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[InternationalAddress] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getApplicantLegalContactName {
        contactName =>

          val preparedForm = request.userAnswers.get(ApplicantLegalContactInternationalAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getApplicantLegalContactName {
        contactName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ApplicantLegalContactInternationalAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ApplicantLegalContactInternationalAddressPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getApplicantLegalContactName(block: String => Future[Result])
                                          (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(ApplicantLegalContactNameQuery).map {
      applicantLegalContactName =>

        block(applicantLegalContactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))

  }
}
