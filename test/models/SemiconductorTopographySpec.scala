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

package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

class SemiconductorTopographySpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "SemiconductorTopography" - {

    "must deserialise" in {

      forAll(arbitrary[String]) { description =>
        val json = Json.obj(
          "rightsType"  -> "semiconductorTopography",
          "description" -> description
        )

        json.validate[SemiconductorTopography] mustEqual JsSuccess(SemiconductorTopography(description))
      }
    }

    "must fail to deserialise for a rightsType other than semiconductorTopography" in {

      forAll(arbitrary[String], Gen.alphaStr) { (description, rightsType) =>
        whenever(rightsType != "semiconductorTopography") {

          val json = Json.obj(
            "rightsType"  -> rightsType,
            "description" -> description
          )

          json.validate[SemiconductorTopography] mustEqual JsError(
            "rightsType must be `semiconductorTopography`"
          )
        }
      }
    }
  }
}
