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

class CopyrightSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  "Copyright" - {

    "must deserialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "copyright",
            "description" -> description
          )

          json.validate[Copyright] mustEqual JsSuccess(Copyright(description))
      }
    }

    "must fail to deserialise for a rightsType other than copyright" in {

      forAll(arbitrary[String], Gen.alphaStr) {
        (description, rightsType) =>

          whenever (rightsType != "copyright") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "description" -> description
            )

            json.validate[Copyright] mustEqual JsError(
              "rightsType must be `copyright`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String]) {
        description =>

          val json = Json.obj(
            "rightsType" -> "copyright",
            "description" -> description
          )

          Json.toJson(Copyright(description))(Copyright.writes) mustEqual json
      }
    }
  }
}
