/*
 * Copyright 2022 HM Revenue & Customs
 *
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
      dataToReturn.right.map {
        data =>
          OptionalDataRequest(request.request, request.identifier, request.name, data)
      }
    }
}
