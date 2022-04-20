/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import models.AfaId
import models.requests.IdentifierRequest
import play.api.mvc.Result
import play.api.mvc.Results.Redirect

import scala.concurrent.{ExecutionContext, Future}

class FakeLockAfaActionProvider(getLock: Boolean = true, afaLockedUrl: String = "/") extends LockAfaActionProvider {

  def apply(afaId: AfaId): LockAction =
    new FakeLockAfaAction(getLock, afaLockedUrl)
}

class FakeLockAfaAction(getLock: Boolean, afaLockedUrl: String) extends LockAction {

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] =
    if (getLock) {
      Future.successful(None)
    } else {
      Future.successful(Some(Redirect(afaLockedUrl)))
    }
}
