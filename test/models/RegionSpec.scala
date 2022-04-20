/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsError, JsString, JsSuccess, Json}
import play.api.mvc.PathBindable

class RegionSpec extends AnyFreeSpec with Matchers with EitherValues {

  "a region" - {

    val bindable = implicitly[PathBindable[Region]]

    "must bind England And Wales from a url" in {
      bindable.bind("", "england-and-wales").right.value mustEqual Region.EnglandAndWales
    }

    "must bind Scotland from a url" in {
      bindable.bind("", "scotland").right.value mustEqual Region.Scotland
    }

    "must bind Northern Ireland from a url" in {
      bindable.bind("", "northern-ireland").right.value mustEqual Region.NorthernIreland
    }

    "must fail to bind an invalid string" in {
      bindable.bind("", "asdfsdf").isLeft mustBe true
    }

    "must unbind to england-and-wales" in {
      bindable.unbind("", Region.EnglandAndWales) mustEqual "england-and-wales"
    }

    "must unbind to scotland" in {
      bindable.unbind("", Region.Scotland) mustEqual "scotland"
    }

    "must unbind to northern-ireland" in {
      bindable.unbind("", Region.NorthernIreland) mustEqual "northern-ireland"
    }

    "must serialise to england-and-wales" in {
      Json.toJson(Region.EnglandAndWales) mustEqual JsString("england-and-wales")
    }

    "must serialise to scotland" in {
      Json.toJson(Region.Scotland) mustEqual JsString("scotland")
    }

    "must serialise to northern-ireland" in {
      Json.toJson(Region.NorthernIreland) mustEqual JsString("northern-ireland")
    }

    "must deserialise from england-and-wales" in {
      JsString("england-and-wales").validate[Region] mustEqual JsSuccess(Region.EnglandAndWales)
    }

    "must deserialise from scotland" in {
      JsString("scotland").validate[Region] mustEqual JsSuccess(Region.Scotland)
    }

    "must deserialise from northern-ireland" in {
      JsString("northern-ireland").validate[Region] mustEqual JsSuccess(Region.NorthernIreland)
    }

    "must fail to deserialise from an invalid string" in {
      JsString("blah").validate[Region] mustBe a[JsError]
    }
  }
}
