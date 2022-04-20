/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class IpRightsTypeSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "IpRightsType" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(IpRightsType.values.toSeq)

      forAll(gen) {
        ipRightsType =>

          JsString(ipRightsType.toString).validate[IpRightsType].asOpt.value mustEqual ipRightsType
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!IpRightsType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[IpRightsType] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(IpRightsType.values.toSeq)

      forAll(gen) {
        ipRightsType =>

          Json.toJson(ipRightsType) mustEqual JsString(ipRightsType.toString)
      }
    }
  }
}
