/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject
import controllers.actions._
import forms.CompanyApplyingInternationalAddressFormProvider
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.CompanyApplyingInternationalAddressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.CompanyApplyingNameQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CompanyApplyingInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class CompanyApplyingInternationalAddressController @Inject()(
                                                               override val messagesApi: MessagesApi,
                                                               afaService: AfaService,
                                                               navigator: Navigator,
                                                               identify: IdentifierAction,
                                                               getLock: LockAfaActionProvider,
                                                               getData: AfaDraftDataRetrievalAction,
                                                               requireData: DataRequiredAction,
                                                               formProvider: CompanyApplyingInternationalAddressFormProvider,
                                                               val controllerComponents: MessagesControllerComponents,
                                                               view: CompanyApplyingInternationalAddressView
                                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        contactName =>

          val preparedForm = request.userAnswers.get(CompanyApplyingInternationalAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        contactName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(CompanyApplyingInternationalAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(CompanyApplyingInternationalAddressPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getCompanyApplyingName(block: String => Future[Result])
                                          (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(CompanyApplyingNameQuery).map {
      companyApplyingName =>

        block(companyApplyingName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))

  }
}
