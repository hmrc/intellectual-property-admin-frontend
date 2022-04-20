/*
 * Copyright 2022 HM Revenue & Customs
 *
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

class SupplementaryProtectionCertificateSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators {

  "SupplementaryProtectionCertificate" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String]) {
        (registrationNumber, registrationEnd, description, certificateType) =>

          val json = Json.obj(
            "rightsType"         -> "supplementaryProtectionCertificate",
            "registrationNumber" -> registrationNumber,
            "registrationEnd"    -> registrationEnd,
            "certificateType"    -> certificateType,
            "description"        -> description
          )

          json.validate[SupplementaryProtectionCertificate]
            .mustEqual(JsSuccess(SupplementaryProtectionCertificate(certificateType, registrationNumber, registrationEnd, description)))
      }
    }

    "must fail to deserialise for a rightsType other than supplementaryProtectionCertificate" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String], Gen.alphaStr) {
        (registrationNumber, registrationEnd, description, certificateType, rightsType) =>

          whenever(rightsType != "supplementaryProtectionCertificate") {

            val json = Json.obj(
              "rightsType" -> rightsType,
              "registrationNumber" -> registrationNumber,
              "registrationEnd"    -> registrationEnd,
              "certificateType"    -> certificateType,
              "description"        -> description
            )

            json.validate[SupplementaryProtectionCertificate] mustEqual JsError(
              "rightsType must be `supplementaryProtectionCertificate`"
            )
          }
      }
    }
  }
}
