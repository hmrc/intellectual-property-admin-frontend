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

package services

import connectors.LockConnector
import models.{AfaId, Lock, LockedException}
import uk.gov.hmrc.http.HeaderCarrier
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultLockService @Inject() (locksConnector: LockConnector)(implicit ec: ExecutionContext) extends LockService {

  def lock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean] =
    locksConnector.lock(afaId).recover { case LockedException(_, _) =>
      false
    }

  def getExistingLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[Lock]] =
    locksConnector.getExistingLock(afaId)

  def lockList(implicit hc: HeaderCarrier): Future[List[Lock]] =
    locksConnector.lockList

  def removeLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Unit] =
    locksConnector.removeLock(afaId)

  def replaceLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean] =
    locksConnector.replaceLock(afaId)

}

trait LockService {

  def lock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean]

  def getExistingLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[Lock]]

  def lockList(implicit hc: HeaderCarrier): Future[List[Lock]]

  def removeLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Unit]

  def replaceLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean]
}
