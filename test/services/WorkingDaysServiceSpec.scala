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

package services

import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import models.Region
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global

class WorkingDaysServiceSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with WireMockHelper
    with ScalaCheckPropertyChecks
    with Generators
    with ScalaFutures
    with IntegrationPatience
    with OptionValues {

  implicit lazy val arbitraryHC: Arbitrary[HeaderCarrier] =
    Arbitrary(Gen.const(HeaderCarrier()))

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.intellectual-property.port" -> server.port
      )
      .build()

  private lazy val service = app.injector.instanceOf[WorkingDaysService]

  "working days service" - {

    "must return a date when one can be calculated" in {

      val pastDate   = LocalDate.now.minusYears(1)
      val futureDate = LocalDate.now.plusYears(1)
      val now        = LocalDate.now
      val daysToAdd  = Gen.choose(1, 365)

      forAll(
        datesBetween(pastDate, now),
        datesBetween(now, futureDate),
        daysToAdd,
        arbitrary[Region],
        arbitrary[HeaderCarrier]
      ) { (inputDate, replyDate, daysToAdd, region, hc) =>
        server.stubFor(
          get(urlEqualTo(s"/intellectual-property/working-days/$region/$inputDate/$daysToAdd"))
            .willReturn(
              ok(
                Json
                  .obj(
                    "date" -> replyDate
                  )
                  .toString
              )
            )
        )

        service.workingDays(region, inputDate, daysToAdd)(implicitly, hc).futureValue mustEqual replyDate
      }
    }

    "must throw an exception when the server call fails" in {

      val statuses  = Gen.chooseNum(201, 599, 400, 499, 500)
      val pastDate  = LocalDate.now.minusYears(1)
      val now       = LocalDate.now
      val daysToAdd = Gen.choose(1, 365)

      forAll(statuses, datesBetween(pastDate, now), daysToAdd, arbitrary[Region], arbitrary[HeaderCarrier]) {
        (status, inputDate, daysToAdd, region, hc) =>
          server.stubFor(
            get(urlEqualTo(s"/intellectual-property/working-days/$region/$inputDate/$daysToAdd"))
              .willReturn(
                aResponse().withStatus(status)
              )
          )

          whenReady(service.workingDays(region, inputDate, daysToAdd)(implicitly, hc).failed) {
            _ mustBe an[Exception]
          }
      }
    }
  }
}
