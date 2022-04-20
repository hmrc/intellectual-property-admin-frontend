/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

import scala.language.implicitConversions

trait IpRight

object IpRight {

  implicit lazy val reads: Reads[IpRight] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    Trademark.reads or
    Copyright.reads or
    Design.reads or
    Patent.reads or
    PlantVariety.reads or
    GeographicalIndication.reads or
    SupplementaryProtectionCertificate.reads or
    SemiconductorTopography.reads
  }
}
