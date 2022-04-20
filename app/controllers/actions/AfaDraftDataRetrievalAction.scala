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
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{AfaId, LockedException}
import play.api.mvc.Results._
import play.api.mvc.{ActionRefiner, Result}
import services.AfaService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AfaDraftDataRetrievalActionProviderImpl @Inject()(afaService: AfaService, ec: ExecutionContext)
  extends AfaDraftDataRetrievalAction {

  def apply(afaId: AfaId): ActionRefiner[IdentifierRequest, OptionalDataRequest] =
    new AfaDraftRetrievalAction(afaId, ec, afaService)
}

class AfaDraftRetrievalAction(
                               afaId: AfaId,
                               implicit protected val executionContext: ExecutionContext,
                               afaService: AfaService) extends ActionRefiner[IdentifierRequest, OptionalDataRequest] {

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    afaService.getDraft(afaId).map {
      userAnswers =>
        Right(OptionalDataRequest(request.request, request.identifier, request.name, userAnswers))
    }.recover {
      case _: LockedException =>
        Left(Redirect(controllers.routes.UnlockAfaController.onPageLoad(afaId)))
    }
  }
}

trait AfaDraftDataRetrievalAction {

  def apply(afaId: AfaId): ActionRefiner[IdentifierRequest, OptionalDataRequest]
}
