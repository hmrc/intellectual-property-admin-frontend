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

package forms

import forms.behaviours.FieldBehaviours
import models.NiceClassId
import org.scalacheck.Gen
import play.api.data.FormError

class IpRightsNiceClassFormProviderSpec extends FieldBehaviours {

  val requiredKey = "ipRightsNiceClass.error.required"
  val lengthKey = "ipRightsNiceClass.error.length"
  val maxLength = 2

  val form = new IpRightsNiceClassFormProvider()(Seq.empty)

  ".value" must {

    val fieldName = "value"

    val validDataGenerator = Gen.choose(1, 45).map(_.toString)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind invalid values" in {

      val validValues = (1 to 45).map(_.toString).toSet

      forAll(stringsExceptSpecificValues(validValues)) {
        invalidValue =>

          val result = form.bind(Map("value" -> invalidValue)).apply("value")
          result.errors shouldEqual Seq(FormError("value", "ipRightsNiceClass.error.format"))
      }
    }

    "not bind duplicate entries" in {

      val niceClass = NiceClassId.fromInt(1).value

      val form = new IpRightsNiceClassFormProvider()(Seq(niceClass))

      val result = form.bind(Map("value" -> "1")).apply("value")
      result.errors shouldEqual Seq(FormError("value", "ipRightsNiceClass.error.duplicate", Seq(niceClass)))
    }
  }
}
