/*
 * Copyright 2022 HM Revenue & Customs
 *
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
