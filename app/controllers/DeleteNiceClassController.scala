/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.DeleteNiceClassFormProvider
import javax.inject.Inject
import models.{AfaId, Mode}
import navigation.Navigator
import pages.DeleteNiceClassPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeleteNiceClassView

import scala.concurrent.{ExecutionContext, Future}

class DeleteNiceClassController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           navigator: Navigator,
                                           identify: IdentifierAction,
                                           getLock: LockAfaActionProvider,
                                           getData: AfaDraftDataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           formProvider: DeleteNiceClassFormProvider,
                                           validateIprIndex: IpRightsIndexActionFilterProvider,
                                           validateNiceClassIndex: NiceClassIndexActionFilterProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DeleteNiceClassView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId, iprIndex: Int, niceClassIndex: Int): Action[AnyContent] = (identify
    andThen getLock(afaId)
    andThen getData(afaId)
    andThen requireData
    andThen validateIprIndex(iprIndex)
    andThen validateNiceClassIndex(iprIndex, niceClassIndex)) {
    implicit request =>

      Ok(view(form, mode, afaId, iprIndex, niceClassIndex))
  }

  def onSubmit(mode: Mode, afaId: AfaId, iprIndex: Int, niceClassIndex: Int): Action[AnyContent] = (identify
    andThen getLock(afaId)
    andThen getData(afaId)
    andThen requireData
    andThen validateIprIndex(iprIndex)
    andThen validateNiceClassIndex(iprIndex, niceClassIndex)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, afaId, iprIndex, niceClassIndex))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeleteNiceClassPage(iprIndex, niceClassIndex), value))
          } yield Redirect(navigator.nextPage(DeleteNiceClassPage(iprIndex, niceClassIndex), mode, updatedAnswers))
        }
      )
  }
}

