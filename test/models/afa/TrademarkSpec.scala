/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import generators.Generators
import models.NiceClassId
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.time.LocalDate

class TrademarkSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators {

  "Trademark" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[Option[String]], arbitrary[String], arbitrary[Seq[NiceClassId]]) {
        (registrationNumber, registrationEnd, brand, description, niceClasses) =>

          val json = brand.map {
            b =>
              Json.obj(
                "rightsType" -> "trademark",
                "registrationNumber" -> registrationNumber,
                "registrationEnd" -> registrationEnd,
                "brand" -> b,
                "description" -> description,
                "niceClasses" -> niceClasses
              )
          }.getOrElse(
            Json.obj(
              "rightsType" -> "trademark",
              "registrationNumber" -> registrationNumber,
              "registrationEnd" -> registrationEnd,
              "description" -> description,
              "niceClasses" -> niceClasses
            )
          )

          json.validate[Trademark] mustEqual JsSuccess(Trademark(registrationNumber, registrationEnd, brand, description, niceClasses))
      }
    }

    "must fail to deserialise for a rightsType other than trademark" in {

      forAll(arbitrary[String], Gen.alphaStr, arbitraryDates, arbitrary[Option[String]], arbitrary[String], arbitrary[Seq[NiceClassId]]) {
        (registrationNumber, rightsType, registrationEnd, brand, description, niceClasses) =>

          whenever(rightsType != "trademark") {

            val json = brand.map {
              b =>
                Json.obj(
                  "rightsType" -> rightsType,
                  "registrationNumber" -> registrationNumber,
                  "registrationEnd" -> registrationEnd,
                  "brand" -> b,
                  "description" -> description,
                  "niceClasses" -> niceClasses
                )
            }.getOrElse(
              Json.obj(
                "rightsType" -> rightsType,
                "registrationNumber" -> registrationNumber,
                "registrationEnd" -> registrationEnd,
                "description" -> description,
                "niceClasses" -> niceClasses
              )
            )

            json.validate[Trademark] mustEqual JsError(
              "rightsType must be `trademark`"
            )
          }
      }
    }

    "must serialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[Option[String]], arbitrary[String], arbitrary[Seq[NiceClassId]]) {
        (registrationNumber, registrationEnd, brand, description, niceClasses) =>

          val json = brand.map {
            b =>
              Json.obj(
                "rightsType" -> "trademark",
                "registrationNumber" -> registrationNumber,
                "registrationEnd" -> registrationEnd,
                "brand" -> b,
                "description" -> description,
                "niceClasses" -> niceClasses
              )
          }.getOrElse {
            Json.obj(
              "rightsType" -> "trademark",
              "registrationNumber" -> registrationNumber,
              "registrationEnd" -> registrationEnd,
              "description" -> description,
              "niceClasses" -> niceClasses
            )
          }

          Json.toJson(Trademark(registrationNumber, registrationEnd, brand, description, niceClasses))(Trademark.writes) mustEqual json
      }
    }
  }
}
