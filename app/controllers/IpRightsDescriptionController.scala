/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.IpRightsDescriptionFormProvider

import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IpRightsDescriptionPage, IpRightsTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsDescriptionView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsDescriptionController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               afaService: AfaService,
                                               navigator: Navigator,
                                               identify: IdentifierAction,
                                               getLock: LockAfaActionProvider,
                                               getData: AfaDraftDataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               validateIndex: IpRightsIndexActionFilterProvider,
                                               formProvider: IpRightsDescriptionFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: IpRightsDescriptionView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[String] = formProvider()

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>

            val preparedForm = request.userAnswers.get(IpRightsDescriptionPage(index)) match {
              case None => form
              case Some(value) => form.fill(value)
            }

            val ipRightType = request.userAnswers.get(IpRightsTypePage(index))

            Future.successful(Ok(view(preparedForm, mode, index, afaId, ipRightType)))
    }

  def onSubmit(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {
      implicit request =>

        val ipRightType = request.userAnswers.get(IpRightsTypePage(index))

            form.bindFromRequest().fold(
              (formWithErrors: Form[_]) =>
                Future.successful(BadRequest(view(formWithErrors, mode, index, afaId, ipRightType))),

              value => {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(IpRightsDescriptionPage(index), value))
                  _              <- afaService.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(IpRightsDescriptionPage(index), mode, updatedAnswers))
              }
            )
    }
}
