/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import com.google.inject.Inject
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

class StrideIdentifierAction @Inject()(
                                        override val authConnector: AuthConnector,
                                        config: Configuration,
                                        val parser: BodyParsers.Default
                                      )(implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val loginUrl = config.get[String]("login.url")
    val loginContinueUrl = config.get[String]("login.continue-url")
    val role = config.get[String]("login.role")

    authorised(AuthProviders(PrivilegedApplication) and Enrolment(role)).retrieve(Retrievals.credentials and Retrievals.name) {
      case Some(Credentials(providerId, _)) ~ Some(Name(Some(name), _)) =>
        block(IdentifierRequest(request, providerId, name))
      case _ =>
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad()))
    } recover {
      case _: NoActiveSession =>
        Redirect(loginUrl, Map("successURL" -> Seq(loginContinueUrl)))
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad())
    }
  }
}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
