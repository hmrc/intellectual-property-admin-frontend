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

class IpRightsSupplementaryProtectionCertificateTypeSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "IpRightsSupplementaryProtectionCertificateType" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(IpRightsSupplementaryProtectionCertificateType.values.toSeq)

      forAll(gen) {
        ipRightsSupplementaryProtectionCertificateType =>

          JsString(ipRightsSupplementaryProtectionCertificateType.toString).validate[IpRightsSupplementaryProtectionCertificateType].asOpt.value mustEqual ipRightsSupplementaryProtectionCertificateType
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!IpRightsSupplementaryProtectionCertificateType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[IpRightsSupplementaryProtectionCertificateType] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(IpRightsSupplementaryProtectionCertificateType.values.toSeq)

      forAll(gen) {
        ipRightsSupplementaryProtectionCertificateType =>

          Json.toJson(ipRightsSupplementaryProtectionCertificateType) mustEqual JsString(ipRightsSupplementaryProtectionCertificateType.toString)
      }
    }
  }
}
