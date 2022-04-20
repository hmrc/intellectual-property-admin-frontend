/*
 * Copyright 2022 HM Revenue & Customs
 *
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
