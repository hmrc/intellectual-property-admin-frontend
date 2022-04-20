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

package controllers.actions

import com.google.inject.{Inject, Singleton}
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

@Singleton
class StrideApiIdentifierAction @Inject()(
                                           override val authConnector: AuthConnector,
                                           config: Configuration,
                                           val parser: BodyParsers.Default
                                         )(implicit val executionContext: ExecutionContext) extends ApiIdentifierAction with AuthorisedFunctions {

  private val role: String = config.get[String]("login.role")

  override protected def refine[A](request: Request[A]): Future[Either[Result, IdentifierRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(AuthProviders(PrivilegedApplication) and Enrolment(role)).retrieve(Retrievals.credentials and Retrievals.name) {
      case Some(Credentials(providerId, _)) ~ Some(Name(Some(name), _)) =>
        Future.successful(Right(IdentifierRequest(request, providerId, name)))
      case _ =>
        Future.successful(Left(Unauthorized))
    }.recover {
      case _ =>
        Left(Unauthorized)
    }
  }
}

trait ApiIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionRefiner[Request, IdentifierRequest]
