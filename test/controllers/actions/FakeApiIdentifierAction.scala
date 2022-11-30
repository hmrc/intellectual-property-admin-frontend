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

import com.google.inject.Inject
import models.requests.IdentifierRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

final case class SuccessfulIdentifierActionConfig(identifier: String, name: String)

class FakeApiIdentifierAction @Inject() (
  config: SuccessfulIdentifierActionConfig,
  bodyParser: PlayBodyParsers
)(implicit val executionContext: ExecutionContext)
    extends ApiIdentifierAction {

  override protected def refine[A](request: Request[A]): Future[Either[Result, IdentifierRequest[A]]] =
    Future.successful(Right(IdentifierRequest(request, config.identifier, config.name)))

  override def parser: BodyParser[AnyContent] = bodyParser.defaultBodyParser
}
