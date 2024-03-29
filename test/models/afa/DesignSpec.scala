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

package models.afa

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.time.LocalDate

class DesignSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators {

  "Design" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
        (registrationNumber, registrationEnd, description) =>
          val json = Json.obj(
            "rightsType"         -> "design",
            "registrationNumber" -> registrationNumber,
            "registrationEnd"    -> registrationEnd,
            "description"        -> description
          )

          json.validate[Design] mustEqual JsSuccess(Design(registrationNumber, registrationEnd, description))
      }
    }

    "must fail to deserialise for a rightsType other than design" in {

      forAll(arbitrary[String], Gen.alphaStr, arbitraryDates, arbitrary[String]) {
        (registrationNumber, rightsType, registrationEnd, description) =>
          whenever(rightsType != "design") {

            val json = Json.obj(
              "rightsType"         -> rightsType,
              "registrationNumber" -> registrationNumber,
              "registrationEnd"    -> registrationEnd,
              "description"        -> description
            )

            json.validate[Design] mustEqual JsError(
              "rightsType must be `design`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
        (registrationNumber, registrationEnd, description) =>
          val json = Json.obj(
            "rightsType"         -> "design",
            "registrationNumber" -> registrationNumber,
            "registrationEnd"    -> registrationEnd,
            "description"        -> description
          )

          Json.toJson(Design(registrationNumber, registrationEnd, description))(Design.writes) mustEqual json
      }
    }
  }
}
