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

package models.afa

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.time.LocalDate

class SupplementaryProtectionCertificateSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with Generators {

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

          json
            .validate[SupplementaryProtectionCertificate]
            .mustEqual(
              JsSuccess(
                SupplementaryProtectionCertificate(certificateType, registrationNumber, registrationEnd, description)
              )
            )
      }
    }

    "must fail to deserialise for a rightsType other than supplementaryProtectionCertificate" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String], Gen.alphaStr) {
        (registrationNumber, registrationEnd, description, certificateType, rightsType) =>
          whenever(rightsType != "supplementaryProtectionCertificate") {

            val json = Json.obj(
              "rightsType"         -> rightsType,
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

    "must serialise" in {

      forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String]) {
        (registrationNumber, registrationEnd, description, certificateType) =>
          val json = Json.obj(
            "rightsType"         -> "supplementaryProtectionCertificate",
            "registrationNumber" -> registrationNumber,
            "registrationEnd"    -> registrationEnd,
            "certificateType"    -> certificateType,
            "description"        -> description
          )

          Json
            .toJson(
              SupplementaryProtectionCertificate(certificateType, registrationNumber, registrationEnd, description)
            )(SupplementaryProtectionCertificate.writes)
            .mustEqual(json)
      }
    }
  }
}
