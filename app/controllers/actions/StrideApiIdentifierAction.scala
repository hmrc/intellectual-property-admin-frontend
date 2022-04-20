/*
 * Copyright 2022 HM Revenue & Customs
 *
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
