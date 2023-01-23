/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.{AfaId, Lock, LockedException, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{INTERNAL_SERVER_ERROR, LOCKED, NOT_FOUND, NO_CONTENT}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import java.time.LocalDateTime

class LockConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with WireMockHelper
    with ScalaCheckPropertyChecks
    with ScalaFutures
    with IntegrationPatience {

  implicit lazy val arbitraryHC: Arbitrary[HeaderCarrier] =
    Arbitrary(Gen.const(HeaderCarrier()))

  val afaId: AfaId                   = AfaId.fromString("UK20190123").get
  val afaId2: AfaId                  = AfaId.fromString("UK20190125").get
  val jsonObj: JsObject              = Json.obj("value" -> afaId)
  val lastUpdated: LocalDateTime     = LocalDateTime.now()
  lazy val userAnswers: UserAnswers  = UserAnswers(afaId, jsonObj, lastUpdated)
  lazy val userAnswers2: UserAnswers = UserAnswers(afaId2, jsonObj, lastUpdated)

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.intellectual-property.port" -> server.port
      )
      .build()

  private lazy val connector: LockConnector = app.injector.instanceOf[LockConnector]

  "lock" - {

    "When the server returns NO_CONTENT" - {

      "must return true" in {

        server.stubFor(
          post(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/lock"))
            willReturn (
              aResponse().withStatus(NO_CONTENT)
            )
        )

        whenReady(connector.lock(afaId)(hc)) { result =>
          result mustBe true
        }

      }
    }

    "when the server returns LOCKED" - {

      "must return a failed future with a LockedException" in {

        server.stubFor(
          post(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/lock"))
            .willReturn(
              aResponse()
                .withStatus(LOCKED)
                .withBody(Json.obj("userId" -> "id", "name" -> "name").toString)
            )
        )

        whenReady(connector.lock(afaId)(hc).failed) { case e: LockedException =>
          e.userId mustEqual "id"
          e.name mustEqual "name"
        }

      }

    }
  }

  "replaceLock" - {

    "when the server returns a successful response" - {

      "must return true" in {

        server.stubFor(
          post(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/replace"))
            .willReturn(
              ok("true")
            )
        )

        connector.replaceLock(afaId)(hc).futureValue mustEqual true

      }
    }

    "when the server INTERNAL_SERVER_ERROR" - {

      "must throw an exception" in {

        server.stubFor(
          post(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/replace"))
            .willReturn(
              aResponse().withStatus(INTERNAL_SERVER_ERROR)
            )
        )

        whenReady(connector.replaceLock(afaId)(hc).failed) {
          _ mustBe an[Exception]
        }

      }
    }
  }

  "removeLock" - {

    "When the server returns NO_CONTENT" - {

      "must return true" in {

        server.stubFor(
          delete(urlEqualTo(s"/intellectual-property/draft-locks/$afaId"))
            willReturn (
              aResponse().withStatus(NO_CONTENT)
            )
        )

        whenReady(connector.removeLock(afaId)(hc)) { result =>
          result mustBe {}
        }
      }

    }

  }

  "getExistingLock" - {

    val lock: Lock = Lock(afaId, "foo", "bar")

    "when the server returns a lock" - {

      "must return the lock" in {

        server.stubFor(
          get(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/lock"))
            .willReturn(
              ok(Json.toJson(lock).toString)
            )
        )

        whenReady(connector.getExistingLock(lock._id)(hc)) { result =>
          result.get mustEqual lock
        }

      }
    }

    "when the server returns NOT_FOUND" - {

      "must return None" in {

        server.stubFor(
          get(urlEqualTo(s"/intellectual-property/draft-locks/$afaId/lock"))
            .willReturn(
              aResponse().withStatus(NOT_FOUND)
            )
        )

        whenReady(connector.getExistingLock(lock._id)(hc)) { result =>
          result must not be defined
        }

      }
    }
  }

  "lockList" - {

    val lockList: List[Lock] = List(Lock(afaId, "foo", "bar"), Lock(afaId, "foot", "bart"))

    "when the server returns success" - {

      "must return the lock list" in {

        server.stubFor(
          get(urlEqualTo("/intellectual-property/draft-locks/list"))
            .willReturn(
              ok(Json.toJson(lockList).toString)
            )
        )

        whenReady(connector.lockList(hc)) { result =>
          result mustEqual lockList
        }

      }
    }

  }
}
