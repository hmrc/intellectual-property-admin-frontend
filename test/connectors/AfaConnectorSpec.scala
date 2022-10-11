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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import generators.AfaGenerators
import models.afa.{InitialAfa, PublishedAfa}
import models.{AfaId, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class AfaConnectorSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite
  with WireMockHelper with ScalaCheckPropertyChecks with ScalaFutures with IntegrationPatience with AfaGenerators {

  implicit lazy val arbitraryHC: Arbitrary[HeaderCarrier] =
    Arbitrary(Gen.const(HeaderCarrier()))

  val afaId: AfaId = AfaId.fromString("UK20190123").get
  val afaId2: AfaId = AfaId.fromString("UK20190125").get
  val jsonObj: JsObject = Json.obj("value" -> afaId)
  val lastUpdated: LocalDateTime = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS)
  lazy val userAnswers: UserAnswers = UserAnswers(afaId, jsonObj, lastUpdated)
  lazy val userAnswers2: UserAnswers = UserAnswers(afaId2, jsonObj, lastUpdated)

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.intellectual-property.port" -> server.port
      )
      .build()

  private lazy val connector: AfaConnector = app.injector.instanceOf[AfaConnector]

  "submit" - {

    "must return true" - {

      "when a valid submission is sent" in {

        forAll(arbitrary[InitialAfa], arbitrary[PublishedAfa], arbitrary[HeaderCarrier]) {
          (initialAfa, publishedAfa, hc) =>

            server.stubFor(
              put(urlEqualTo(s"/intellectual-property/afa/${initialAfa.id.toString}"))
                .willReturn(
                  ok(Json.toJson(publishedAfa).toString)
                )
            )

            whenReady(connector.submit(initialAfa)(implicitly, hc)) {
              result =>
                result mustEqual publishedAfa
            }
        }
      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[InitialAfa], arbitrary[PublishedAfa], arbitrary[HeaderCarrier], statuses) {
          (initialAfa, publishedAfa, hc, returnStatus) =>

            server.stubFor(
              put(urlEqualTo(s"/intellectual-property/afa/${initialAfa.id.toString}"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.submit(initialAfa)(implicitly, hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "bulkInsert" - {

    "must return a count of the afas inserted" - {

      "when the server returns 200" in {

        forAll(Gen.nonEmptyListOf(arbitrary[PublishedAfa]), arbitrary[HeaderCarrier]) {
          (afas, hc) =>

            server.stubFor(
              post(urlEqualTo(s"/intellectual-property/afa/test-only/bulk-upsert"))
                .willReturn(
                  ok(Json.toJson(afas.size).toString)
                )
            )

            whenReady(connector.bulkInsert(afas)(implicitly, hc)) {
              result =>
                result mustEqual afas.size
            }
        }
      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(Gen.nonEmptyListOf(arbitrary[PublishedAfa]), arbitrary[HeaderCarrier], statuses) {
          (afas, hc, returnStatus) =>

            server.stubFor(
              post(urlEqualTo(s"/intellectual-property/afa/test-only/bulk-upsert"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.bulkInsert(afas)(implicitly, hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "getAfaId" - {

    "must return an AfaId" - {

      "when successful" in {

        forAll(arbitrary[HeaderCarrier], arbitrary[AfaId]) {
          (hc, afaId) =>

            server.stubFor(
              get(urlEqualTo(s"/intellectual-property/afaId"))
                .willReturn(
                  ok(Json.obj("value" -> afaId).toString())
                )
            )

            whenReady(connector.getNextAfaId()(implicitly, hc)) {
              result =>
                result mustEqual afaId
            }
        }
      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[HeaderCarrier], statuses) {
          (hc, returnStatus) =>

            server.stubFor(
              get(urlEqualTo(s"/intellectual-property/afaId"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.getNextAfaId()(implicitly, hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "getDraft" - {

    "must return an Afa" - {

      "when successful" in {

        server.stubFor(
          get(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
            .willReturn(
              ok(jsonObj.toString())
            )
        )

        whenReady(connector.getDraft(afaId)(hc)) {
          result =>
            result.get mustBe jsonObj
        }

      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[HeaderCarrier], statuses) {
          (hc, returnStatus) =>

            server.stubFor(
              get(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.getDraft(afaId)(hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "removeDraft" - {

    "must return an Afa" - {

      "when successful" in {

        server.stubFor(
          delete(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
            .willReturn(
              ok(Json.toJson(userAnswers).toString())
            )
        )

        whenReady(connector.removeDraft(afaId)(hc)) {
          result =>
            result.get mustBe userAnswers
        }

      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[HeaderCarrier], statuses) {
          (hc, returnStatus) =>

            server.stubFor(
              delete(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.removeDraft(afaId)(hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "set" - {

    "must set the an Afa" - {

      "when successful" in {

        server.stubFor(
          put(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
            .willReturn(
              ok("true")
            )
        )

        whenReady(connector.set(afaId, userAnswers)(hc)) {
          result =>
            result mustBe true
        }

      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[HeaderCarrier], statuses) {
          (hc, returnStatus) =>

            server.stubFor(
              put(urlEqualTo(s"/intellectual-property/draft-afa/${afaId}"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.set(afaId, userAnswers)(hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }

  "draftList" - {

    "must return the draft afas list" - {

      "when successful" in {

        val draftList: List[UserAnswers] = List(userAnswers, userAnswers2)

        server.stubFor(
          get(urlEqualTo("/intellectual-property/draft-afas/list"))
            .willReturn(
              ok(Json.toJson(draftList).toString())
            )
        )

        whenReady(connector.draftList(hc)) {
          result =>
            result mustBe draftList
        }

      }
    }

    "must throw an exception" - {

      "when the server returns a non 200 status" in {

        val statuses: Gen[Int] =
          Gen.chooseNum(201, 599, 400, 499, 500)

        forAll(arbitrary[HeaderCarrier], statuses) {
          (hc, returnStatus) =>

            server.stubFor(
              get(urlEqualTo("/intellectual-property/draft-afas/list"))
                .willReturn(
                  status(returnStatus)
                )
            )

            whenReady(connector.draftList(hc).failed) {
              _ mustBe an[Exception]
            }
        }
      }
    }
  }
}

