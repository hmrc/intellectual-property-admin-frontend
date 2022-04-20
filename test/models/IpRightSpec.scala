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

package models

import generators.AfaGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class IpRightSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with AfaGenerators {

  "an IP right" - {

    lazy val arbitraryDates = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

    "must deserialise" - {

      "from a copyright" in {

        forAll(arbitrary[String]) {
          description =>

            val json = Json.obj(
              "rightsType" -> "copyright",
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(Copyright(description))
        }
      }

      "from a plantVariety" in {

        forAll(arbitrary[String]) {
          description =>

            val json = Json.obj(
              "rightsType" -> "plantVariety",
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(PlantVariety(description))
        }
      }

      "from a supplementaryProtectionCertificate" in {

        forAll(arbitrary[String], arbitraryDates, arbitrary[String], arbitrary[String]) {
          (registrationNumber, registrationEnd, description, certificateType) =>

            val json = Json.obj(
              "rightsType"         -> "supplementaryProtectionCertificate",
              "registrationNumber" -> registrationNumber,
              "registrationEnd"    -> registrationEnd,
              "certificateType"  -> certificateType,
              "description"        -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(SupplementaryProtectionCertificate(certificateType, registrationNumber, registrationEnd, description))
        }
      }

      "from a semiconductorTopography" in {

        forAll(arbitrary[String]) {
          description =>

            val json = Json.obj(
              "rightsType" -> "semiconductorTopography",
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(SemiconductorTopography(description))
        }
      }

      "from a geographicalIndication" in {

        forAll(arbitrary[String]) {
          description =>

            val json = Json.obj(
              "rightsType" -> "geographicalIndication",
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(GeographicalIndication(description))
        }
      }

      "from a design" in {

        forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
          (registrationNumber, registrationEnd, description) =>

            val json = Json.obj(
              "rightsType" -> "design",
              "registrationNumber" -> registrationNumber,
              "registrationEnd" -> registrationEnd,
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(Design(registrationNumber, registrationEnd, description))
        }
      }

      "from a patent" in {

        forAll(arbitrary[String], arbitraryDates, arbitrary[String]) {
          (registrationNumber, registrationEnd, description) =>

            val json = Json.obj(
              "rightsType" -> "patent",
              "registrationNumber" -> registrationNumber,
              "registrationEnd" -> registrationEnd,
              "description" -> description
            )

            json.validate[IpRight] mustEqual JsSuccess(Patent(registrationNumber, registrationEnd, description))
        }
      }

      "from a trademark" in {

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

            json.validate[IpRight] mustEqual JsSuccess(Trademark(registrationNumber, registrationEnd, Some(brand), description, niceClasses))
        }
      }
    }
  }
}
