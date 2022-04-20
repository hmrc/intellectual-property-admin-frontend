/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.RepresentativeContactUkAddressFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode, UkAddress}
import navigation.Navigator
import pages.{RepresentativeContactUkAddressPage, RepresentativeDetailsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.RepresentativeContactUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeContactUkAddressController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          afaService: AfaService,
                                                          navigator: Navigator,
                                                          identify: IdentifierAction,
                                                          getLock: LockAfaActionProvider,
                                                          getData: AfaDraftDataRetrievalAction,
                                                          requireData: DataRequiredAction,
                                                          formProvider: RepresentativeContactUkAddressFormProvider,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          view: RepresentativeContactUkAddressView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[UkAddress] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        contactName =>

          val preparedForm = request.userAnswers.get(RepresentativeContactUkAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, mode, contactName, afaId)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getRepresentativeContactName {
        contactName =>

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, contactName, afaId))),

            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(RepresentativeContactUkAddressPage, value))
                _ <- afaService.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(RepresentativeContactUkAddressPage, mode, updatedAnswers))
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
