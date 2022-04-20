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

class CompanyApplyingIsRightsHolderSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "CompanyApplyingIsRightsHolder" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(CompanyApplyingIsRightsHolder.values.toSeq)

      forAll(gen) {
        companyApplyingIsRightsHolder =>

          JsString(companyApplyingIsRightsHolder.toString).validate[CompanyApplyingIsRightsHolder].asOpt.value mustEqual companyApplyingIsRightsHolder
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CompanyApplyingIsRightsHolder.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[CompanyApplyingIsRightsHolder] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(CompanyApplyingIsRightsHolder.values.toSeq)

      forAll(gen) {
        companyApplyingIsRightsHolder =>

          Json.toJson(companyApplyingIsRightsHolder) mustEqual JsString(companyApplyingIsRightsHolder.toString)
      }
    }
  }
}
