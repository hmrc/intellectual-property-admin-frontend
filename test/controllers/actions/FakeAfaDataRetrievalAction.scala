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

import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{AfaId, UserAnswers}
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class FakeAfaDraftDataRetrievalActionProvider(dataToReturn: Option[UserAnswers]) extends AfaDraftDataRetrievalAction {

  def apply(afaId: AfaId): ActionRefiner[IdentifierRequest, OptionalDataRequest] =
    new FakeAfaDraftDataRetrievalAction(dataToReturn)
}

class FakeAfaDraftDataRetrievalAction(
                                          dataToReturn: Either[Result, Option[UserAnswers]]
                                        ) extends ActionRefiner[IdentifierRequest, OptionalDataRequest] {

  def this(dataToReturn: Option[UserAnswers]) =
    this(Right(dataToReturn))

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] =
    Future.successful {
      dataToReturn.map {
        data =>
          OptionalDataRequest(request.request, request.identifier, request.name, data)
      }
    }
}
