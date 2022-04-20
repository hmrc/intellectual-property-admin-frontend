/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.SecondaryTechnicalContactUkAddressFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode, UkAddress}
import navigation.Navigator
import pages.{SecondaryTechnicalContactUkAddressPage, WhoIsSecondaryTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SecondaryTechnicalContactUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class SecondaryTechnicalContactUkAddressController @Inject()(
                                                              override val messagesApi: MessagesApi,
                                                              afaService: AfaService,
                                                              navigator: Navigator,
                                                              identify: IdentifierAction,
                                                              getLock: LockAfaActionProvider,
                                                              getData: AfaDraftDataRetrievalAction,
                                                              requireData: DataRequiredAction,
                                                              formProvider: SecondaryTechnicalContactUkAddressFormProvider,
                                                              val controllerComponents: MessagesControllerComponents,
                                                              view: SecondaryTechnicalContactUkAddressView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[UkAddress] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getSecondaryTechnicalContactName {
        contactName =>

          val preparedForm = request.userAnswers.get(SecondaryTechnicalContactUkAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getSecondaryTechnicalContactName {
        contactName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(SecondaryTechnicalContactUkAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(SecondaryTechnicalContactUkAddressPage, mode, updatedAnswers))
            }
          )
      }
  }

  private def getSecondaryTechnicalContactName(block: String => Future[Result])
                                          (implicit request: DataRequest[AnyContent]): Future[Result] = {

    request.userAnswers.get(WhoIsSecondaryTechnicalContactPage).map {
      secondaryTechnicalContactName =>
        block(secondaryTechnicalContactName.contactName)
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))

  }
}
