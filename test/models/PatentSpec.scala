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

package models

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.time.LocalDate

class PatentSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators {

  "Patent" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
        (registrationNumber, registrationEnd, description) =>
          val json = Json.obj(
            "rightsType"         -> "patent",
            "registrationNumber" -> registrationNumber,
            "registrationEnd"    -> registrationEnd,
            "description"        -> description
          )

          json.validate[Patent] mustEqual JsSuccess(Patent(registrationNumber, registrationEnd, description))
      }
    }

    "must fail to deserialise for a rightsType other than patent" in {

      forAll(arbitrary[String], Gen.alphaStr, arbitraryDates, arbitrary[String]) {
        (registrationNumber, rightsType, registrationEnd, description) =>
          whenever(rightsType != "patent") {

            val json = Json.obj(
              "rightsType"         -> rightsType,
              "registrationNumber" -> registrationNumber,
              "registrationEnd"    -> registrationEnd,
              "description"        -> description
            )

            json.validate[Patent] mustEqual JsError(
              "rightsType must be `patent`"
            )
          }
      }
    }
  }
}
