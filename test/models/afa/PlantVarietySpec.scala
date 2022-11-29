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

package models.afa

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

class PlantVarietySpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "PlantVariety" - {

    "must deserialise" in {

      forAll(arbitrary[String]) { description =>
        val json = Json.obj(
          "rightsType"  -> "plantVariety",
          "description" -> description
        )

        json.validate[PlantVariety] mustEqual JsSuccess(PlantVariety(description))
      }
    }

    "must fail to deserialise for a rightsType other than plantVariety" in {

      forAll(arbitrary[String], Gen.alphaStr) { (description, rightsType) =>
        whenever(rightsType != "plantVariety") {

          val json = Json.obj(
            "rightsType"  -> rightsType,
            "description" -> description
          )

          json.validate[PlantVariety] mustEqual JsError(
            "rightsType must be `plantVariety`"
          )
        }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String]) { description =>
        val json = Json.obj(
          "rightsType"  -> "plantVariety",
          "description" -> description
        )

        Json.toJson(PlantVariety(description))(PlantVariety.writes) mustEqual json
      }
    }
  }
}
