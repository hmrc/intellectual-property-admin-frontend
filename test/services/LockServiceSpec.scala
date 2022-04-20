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
import generators.AfaGenerators
import models.{AfaId, Lock}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LockServiceSpec extends AnyFreeSpec with Matchers with TryValues with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with OptionValues with AfaGenerators {

  private val lockConnector = mock[LockConnector]

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    Mockito.reset(lockConnector)
    super.beforeEach()
  }

  val afaId: AfaId = AfaId.fromString("UK20190123").get

  "Lock service" - {

    "when calling .getExistingLock" - {

      "must get the draft AFA existing lock" in {

        val service = new DefaultLockService(lockConnector)
        val lock = Lock(afaId, "foo", "bar")

        when(lockConnector.getExistingLock(afaId)) thenReturn
          Future.successful(Some(lock))

        val result = service.getExistingLock(afaId)
        result.futureValue.value mustBe lock
        whenReady(result) {
          _ =>
            verify(lockConnector, times(1)).getExistingLock(eqTo(afaId))(any())
        }
      }
    }

    "when calling .removeLock" - {

      "must remove the afa draft lock" in {

        val service = new DefaultLockService(lockConnector)

        when(lockConnector.removeLock(afaId) )thenReturn Future.successful((): Unit)

        val result = service.removeLock(afaId)
        result.futureValue mustBe {}

        whenReady(result) {
          _ =>
            verify(lockConnector, times(1)).removeLock(eqTo(afaId))(any())
        }
      }
    }

    "when calling .lockList" - {

      "must retrieve the draft locks list" in {

        val lockList = List(Lock(afaId, "foo", "bar"), Lock(afaId, "foot", "bart"))

        val service = new DefaultLockService(lockConnector)

        when(lockConnector.lockList) thenReturn
          Future.successful(lockList)

        val result = service.lockList
        result.futureValue mustBe lockList

        whenReady(result) {
          _ =>
            verify(lockConnector, times(1)).lockList(any())
        }
      }
    }

    "when calling .replaceLock" - {

      "must replace the draft AFA lock" in {

        val service = new DefaultLockService(lockConnector)

        when(lockConnector.replaceLock(afaId)) thenReturn
          Future.successful(true)

        val result = service.replaceLock(afaId)
        result.futureValue mustBe true
        whenReady(result) {
          _ =>
            verify(lockConnector, times(1)).replaceLock(eqTo(afaId))(any())
        }
      }
    }

    "when calling .lock" - {

      "must apply the draft AFA lock" in {

        val service = new DefaultLockService(lockConnector)

        when(lockConnector.lock(afaId)) thenReturn
          Future.successful(true)

        val result = service.lock(afaId)
        result.futureValue mustBe true
        whenReady(result) {
          _ =>
            verify(lockConnector, times(1)).lock(eqTo(afaId))(any())
        }

      }
    }
  }
}
