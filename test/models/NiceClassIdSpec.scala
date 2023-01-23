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

package models

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class NiceClassIdSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with Generators
    with OptionValues {

  "must serialise" in {

    forAll(arbitrary[NiceClassId]) { niceClassId =>
      Json.toJson(niceClassId) mustEqual JsNumber(niceClassId.value)
    }
  }

  "must deserialise from a valid number" in {

    forAll(Gen.choose(1, 45)) { number =>
      val json = JsNumber(number)

      json.validate[NiceClassId].asOpt.value mustEqual NiceClassId.fromInt(number).value
    }
  }

  "must deserialise from a valid string" in {

    forAll(Gen.choose(1, 45).map(_.toString)) { number =>
      val json = JsString(number)

      json.validate[NiceClassId].asOpt.value mustEqual NiceClassId.fromString(number).value
    }
  }

  "must deserialise from a valid json object" in {
    forAll(Gen.choose(1, 45).map(_.toString)) { number =>
      val json = Json.obj("niceClass" -> number)

      json.validate[NiceClassId].asOpt.value mustEqual NiceClassId.fromString(number).value
    }
  }

  "must fail to deserialise from an invalid number" in {

    val gen = Gen.oneOf(Gen.choose(-100, 0), Gen.choose(46, Int.MaxValue))

    forAll(gen) { invalidNumber =>
      val json = JsNumber(invalidNumber)

      json.validate[NiceClassId] mustEqual JsError("NICE class Id is not valid")
    }
  }

  "must fail to deserialise from an out-of-range string" in {

    val gen = Gen.oneOf(Gen.choose(-100, 0), Gen.choose(46, Int.MaxValue))

    forAll(gen.map(_.toString)) { invalidString =>
      val json = JsString(invalidString)

      json.validate[NiceClassId] mustEqual JsError("NICE class Id is not valid")
    }
  }

  "must fail to deserialise from an invalid string" in {

    forAll(Gen.alphaStr) { invalidString =>
      val json = JsString(invalidString)

      json.validate[NiceClassId] mustEqual JsError("NICE class Id is not valid")
    }
  }
}
