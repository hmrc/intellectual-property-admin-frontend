/*
 * Copyright 2022 HM Revenue & Customs
 *
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

class PatentSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators {

  "Patent" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
        (registrationNumber, registrationEnd, description) =>

          val json = Json.obj(
            "rightsType" -> "patent",
            "registrationNumber" -> registrationNumber,
            "registrationEnd" -> registrationEnd,
            "description" -> description
          )

          json.validate[Patent] mustEqual JsSuccess(Patent(registrationNumber, registrationEnd, description))
      }
    }

    "must fail to deserialise for a rightsType other than patent" in {

      forAll(arbitrary[String], Gen.alphaStr, arbitraryDates, arbitrary[String]) {
        (registrationNumber, rightsType, registrationEnd, description) =>

          whenever (rightsType != "patent") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "registrationNumber" -> registrationNumber,
              "registrationEnd" -> registrationEnd,
              "description" -> description
            )

            json.validate[Patent] mustEqual JsError(
              "rightsType must be `patent`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
        (registrationNumber, registrationEnd, description) =>

          val json = Json.obj(
            "rightsType" -> "patent",
            "registrationNumber" -> registrationNumber,
            "registrationEnd" -> registrationEnd,
            "description" -> description
          )

          Json.toJson(Patent(registrationNumber, registrationEnd, description))(Patent.writes) mustEqual json
      }
    }
  }
}
