/*
 * Copyright 2022 HM Revenue & Customs
 *
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

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "plantVariety",
            "description" -> description
          )

          json.validate[PlantVariety] mustEqual JsSuccess(PlantVariety(description))
      }
    }

    "must fail to deserialise for a rightsType other than plantVariety" in {

      forAll(arbitrary[String], Gen.alphaStr) {
        (description, rightsType) =>

          whenever (rightsType != "plantVariety") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "description" -> description
            )

            json.validate[PlantVariety] mustEqual JsError(
              "rightsType must be `plantVariety`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "plantVariety",
            "description" -> description
          )

          Json.toJson(PlantVariety(description))(PlantVariety.writes) mustEqual json
      }
    }
  }
}
