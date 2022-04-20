/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms.behaviours

import play.api.data.{Form, FormError}
import org.scalacheck.Arbitrary.arbitrary

trait StringFieldBehaviours extends FieldBehaviours {

    def fieldWithMaxLength(form: Form[_],
                           fieldName: String,
                           maxLength: Int,
                           lengthError: FormError): Unit = {

    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors shouldEqual Seq(lengthError)
      }
    }
  }

  def fieldWithRegexp(form: Form[_],
                      fieldName: String,
                      regexp: String,
                      error: FormError): Unit = {

    s"not bind strings which do not match $regexp" in {

      forAll(arbitrary[String].suchThat(_.nonEmpty)) {
        string =>
          whenever(!string.matches(regexp)) {
            val result = form.bind(Map(fieldName -> string))(fieldName)
            result.errors shouldEqual Seq(error)
          }
      }
    }
  }
}
