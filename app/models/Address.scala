/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, OFormat, Reads, Writes}

import scala.language.implicitConversions

final case class UkAddress(
                            line1: String,
                            line2: Option[String],
                            town: String,
                            county: Option[String],
                            postCode: String
                          ) extends Address

object UkAddress {

  implicit lazy val formats: OFormat[UkAddress] = Json.format[UkAddress]
}

final case class InternationalAddress(
                                       line1: String,
                                       line2: Option[String],
                                       town: String,
                                       country: String,
                                       postCode: Option[String]
                                     ) extends Address

object InternationalAddress {

  implicit lazy val formats: OFormat[InternationalAddress] = Json.format[InternationalAddress]
}

sealed trait Address

object Address {

  implicit lazy val reads: Reads[Address] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] = {
        a.map[B](identity).orElse(b)
      }
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    InternationalAddress.formats or
      UkAddress.formats
  }

  implicit lazy val writes: Writes[Address] = Writes {
    case address: UkAddress            => Json.toJson(address)(UkAddress.formats)
    case address: InternationalAddress => Json.toJson(address)(InternationalAddress.formats)
  }
}
