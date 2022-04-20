/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject
import controllers.actions._
import forms.CompanyApplyingUkAddressFormProvider
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.CompanyApplyingUkAddressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.CompanyApplyingNameQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CompanyApplyingUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class CompanyApplyingUkAddressController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    afaService: AfaService,
                                                    navigator: Navigator,
                                                    identify: IdentifierAction,
                                                    getLock: LockAfaActionProvider,
                                                    getData: AfaDraftDataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    formProvider: CompanyApplyingUkAddressFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: CompanyApplyingUkAddressView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyApplyingName {
        contactName =>

          val preparedForm = request.userAnswers.get(CompanyApplyingUkAddressPage) match {
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
                updatedAnswers <- Future.fromTry(request.userAnswers.set(CompanyApplyingUkAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(CompanyApplyingUkAddressPage, mode, updatedAnswers))
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
