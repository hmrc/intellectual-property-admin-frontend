/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package forms.mappings

import java.time.LocalDate

import play.api.data.{FieldMapping, Mapping}
import play.api.data.Forms.of
import models.Enumerable

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String, args: Any*): FieldMapping[String] =
    of(stringFormatter(errorKey, args: _*))

  protected def email: Mapping[String] = of(stringFormatter("email.required"))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric"): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def boolean(requiredKey: String,
                        invalidKey: String,
                        args: Any*): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args: _*))


  protected def enumerable[A](requiredKey: String = "error.required",
                              invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(
                           invalidKey: String,
                           allRequiredKey: String,
                           twoRequiredKey: String,
                           requiredKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))
}
