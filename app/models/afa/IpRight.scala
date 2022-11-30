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

package models.afa

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
      SupplementaryProtectionCertificate.reads or
      SemiconductorTopography.reads or
      GeographicalIndication.reads
  }

  implicit lazy val writes: Writes[IpRight] = Writes {
    case r: Trademark                          => Json.toJson(r)(Trademark.writes)
    case r: Copyright                          => Json.toJson(r)(Copyright.writes)
    case r: Design                             => Json.toJson(r)(Design.writes)
    case r: Patent                             => Json.toJson(r)(Patent.writes)
    case r: PlantVariety                       => Json.toJson(r)(PlantVariety.writes)
    case r: SupplementaryProtectionCertificate => Json.toJson(r)(SupplementaryProtectionCertificate.writes)
    case r: SemiconductorTopography            => Json.toJson(r)(SemiconductorTopography.writes)
    case r: GeographicalIndication             => Json.toJson(r)(GeographicalIndication.writes)
  }
}
