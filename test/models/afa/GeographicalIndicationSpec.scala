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

class GeographicalIndicationSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "GeographicalIndication" - {

    "must deserialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "geographicalIndication",
            "description" -> description
          )

          json.validate[GeographicalIndication] mustEqual JsSuccess(GeographicalIndication(description))
      }
    }

    "must fail to deserialise for a rightsType other than geographicalIndication" in {

      forAll(arbitrary[String], Gen.alphaStr) {
        (description, rightsType) =>

          whenever (rightsType != "geographicalIndication") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "description" -> description
            )

            json.validate[GeographicalIndication] mustEqual JsError(
              "rightsType must be `geographicalIndication`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "geographicalIndication",
            "description" -> description
          )

          Json.toJson(GeographicalIndication(description))(GeographicalIndication.writes) mustEqual json
      }
    }
  }
}
