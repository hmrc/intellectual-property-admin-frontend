/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import com.google.inject.Inject
import models.requests.IdentifierRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

final case class SuccessfulIdentifierActionConfig(identifier: String, name: String)

class FakeApiIdentifierAction @Inject() (
                                             config: SuccessfulIdentifierActionConfig,
                                             bodyParser: PlayBodyParsers
                                           )(implicit val executionContext: ExecutionContext) extends ApiIdentifierAction {

  override protected def refine[A](request: Request[A]): Future[Either[Result, IdentifierRequest[A]]] =
    Future.successful(Right(IdentifierRequest(request, config.identifier, config.name)))

  override def parser: BodyParser[AnyContent] = bodyParser.defaultBodyParser
}
