/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.testonly

import connectors.ConsignmentConnector
import controllers.actions.IdentifierAction
import forms.testonly.InsertSingleConsignmentFormProvider
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.testonly.InsertSingleConsignmentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TestConsignmentController @Inject()(
                                           identify: IdentifierAction,
                                           consignmentConnector: ConsignmentConnector,
                                           view: InsertSingleConsignmentView,
                                           formProvider: InsertSingleConsignmentFormProvider,
                                           val controllerComponents: MessagesControllerComponents
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    Future.successful(Ok(view(form)))
  }

  def submit(): Action[AnyContent] = identify.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors))),
        value => {
          val consignment: JsValue = Json.parse(value)
          val consignmentId: String = (consignment \ "_id").as[String]
          consignmentConnector.submitTestOnlyConsignment(value, consignmentId).map(_ => Ok)
        }
      )
  }
}
