/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import controllers.routes
import models.AfaId
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import services.LockService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class LockAfaAction(
                     afaId: AfaId,
                     implicit protected val executionContext: ExecutionContext,
                     lockService: LockService
                   ) extends LockAction {

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    lockService.lock(afaId).flatMap {
      gotLock =>
        if (gotLock) {
          Future.successful(None)
        } else {
          Future.successful(Some(Redirect(routes.UnlockAfaController.onPageLoad(afaId))))
        }
    }
  }
}

trait LockAction extends ActionFilter[IdentifierRequest]

class LockAfaActionProviderImpl @Inject()(lockService: LockService, ec: ExecutionContext) extends LockAfaActionProvider {

  def apply(afaId: AfaId): LockAction =
    new LockAfaAction(afaId, ec, lockService)
}

trait LockAfaActionProvider {

  def apply(afaId: AfaId): LockAction
}
