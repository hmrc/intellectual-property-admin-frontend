/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.testonly

import connectors.AfaConnector
import controllers.actions.IdentifierAction
import models.afa.PublishedAfa
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BulkInsertAfaController @Inject()(
                                         identify: IdentifierAction,
                                         afaConnector: AfaConnector,
                                         val controllerComponents: MessagesControllerComponents
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def submit(): Action[Seq[PublishedAfa]] = identify.async(parse.json[Seq[PublishedAfa]]) {
    implicit request =>

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      afaConnector.bulkInsert(request.body).map(_ => Ok)
  }
}
