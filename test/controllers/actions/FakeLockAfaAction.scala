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
