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

class SemiconductorTopographySpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "SemiconductorTopography" - {

    "must deserialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "semiconductorTopography",
            "description" -> description
          )

          json.validate[SemiconductorTopography] mustEqual JsSuccess(SemiconductorTopography(description))
      }
    }

    "must fail to deserialise for a rightsType other than semiconductorTopography" in {

      forAll(arbitrary[String], Gen.alphaStr) {
        (description, rightsType) =>

          whenever (rightsType != "semiconductorTopography") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "description" -> description
            )

            json.validate[SemiconductorTopography] mustEqual JsError(
              "rightsType must be `semiconductorTopography`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "semiconductorTopography",
            "description" -> description
          )

          Json.toJson(SemiconductorTopography(description))(SemiconductorTopography.writes) mustEqual json
      }
    }
  }
}
