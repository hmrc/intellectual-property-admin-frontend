/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.Configuration
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class StrideIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: Configuration,
  val parser: BodyParsers.Default,
  appConfig: FrontendAppConfig
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val loginUrl = config.get[String]("login.url")
    val role     = config.get[String]("login.role")

    authorised(AuthProviders(PrivilegedApplication) and Enrolment(role))
      .retrieve(Retrievals.credentials and Retrievals.name) {
        case Some(Credentials(providerId, _)) ~ Some(Name(Some(name), _)) =>
          block(IdentifierRequest(request, providerId, name))
        case _                                                            =>
          Future.successful(Redirect(routes.UnauthorisedController.onPageLoad))
      } recover {
      case _: NoActiveSession        =>
        Redirect(loginUrl, Map("successURL" -> Seq(appConfig.manageIprHomeUrl)))
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }
}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]
