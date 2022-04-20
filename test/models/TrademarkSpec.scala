/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import generators.AfaGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.time.LocalDate

class TrademarkSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with AfaGenerators {

  "Trademark" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String], arbitrary[Seq[NiceClassId]]) {
        (registrationNumber, registrationEnd, brand, description, niceClasses) =>

          val json = Json.obj(
                "rightsType" -> "trademark",
                "registrationNumber" -> registrationNumber,
                "registrationEnd" -> registrationEnd,
                "descriptionWithBrand" -> Json.obj(
                  "brand" -> brand,
                  "description" -> description
                ),
                "niceClasses" -> niceClasses.map {
                  niceClass =>
                    niceClass.value
                }
          )

          json.validate[Trademark] mustEqual JsSuccess(Trademark(registrationNumber, registrationEnd, Some(brand), description, niceClasses))
      }
    }

    "must fail to deserialise for a rightsType other than trademark" in {

      forAll(arbitrary[String], Gen.alphaStr, arbitraryDates, arbitrary[String], arbitrary[String], arbitrary[Seq[NiceClassId]]) {
        (registrationNumber, rightsType, registrationEnd, brand, description, niceClasses) =>

          whenever(rightsType != "trademark") {

            val json = Json.obj(
                  "rightsType" -> rightsType,
                  "registrationNumber" -> registrationNumber,
                  "registrationEnd" -> registrationEnd,
                  "descriptionWithBrand" -> Json.obj(
                    "brand" -> brand,
                    "description" -> description
                  ),
                  "niceClasses" -> niceClasses.map {
                    niceClass =>
                      niceClass.value
                  }
                )

            json.validate[Trademark] mustEqual JsError(
              "rightsType must be `trademark`"
            )
          }
      }
    }
  }
}
