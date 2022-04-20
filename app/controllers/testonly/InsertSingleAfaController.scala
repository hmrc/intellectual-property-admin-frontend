/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.testonly

import connectors.AfaConnector
import controllers.actions.IdentifierAction
import forms.testonly.InsertSingleAfaFormProvider
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.testonly.InsertSingleAfaView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InsertSingleAfaController @Inject()(
                                           identify: IdentifierAction,
                                           afaConnector: AfaConnector,
                                           view: InsertSingleAfaView,
                                           formProvider: InsertSingleAfaFormProvider,
                                           val controllerComponents: MessagesControllerComponents
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async {
    implicit request =>
      Future.successful(Ok(view(form)))
  }

  def submit(): Action[AnyContent] = identify.async {

    implicit request =>

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)


      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          val afa: JsValue = Json.parse(value)
          val afaId: String = (afa \ "id").as[String]
          afaConnector.submitTestOnlyAfa(value, afaId).map(_ => Ok)
        }
      )
  }
}
