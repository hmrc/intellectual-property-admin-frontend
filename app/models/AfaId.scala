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

import java.time.Year

import play.api.libs.json._
import play.api.mvc.PathBindable

import scala.util.Try

final case class AfaId private (year: Year, id: Int, prefix: AfaId.Prefix) {

  require {
    prefix match {
      case AfaId.UK        =>
        id >= 0 && id <= 9999
      case AfaId.GB(true)  =>
        id >= 0 && id <= 999
      case AfaId.GB(false) =>
        id >= 0 && id <= 99
    }
  }

  override def toString: String = prefix match {
    case AfaId.UK        =>
      f"UK$year$id%04d"
    case AfaId.GB(true)  =>
      f"GB$year$id%03d"
    case AfaId.GB(false) =>
      f"GB$year$id%02d"
  }

  def isMigrated: Boolean =
    prefix != AfaId.UK
}

object AfaId {

  sealed trait Prefix
  case object UK extends Prefix
  case class GB(threeDigitId: Boolean = true) extends Prefix
  object GB extends GB(true)

  def apply(yearString: String, idString: String, prefix: Prefix): Try[AfaId] = for {
    year  <- Try(Year.parse(yearString))
    id    <- Try(idString.toInt)
    afaId <- Try(new AfaId(year, id, prefix))
  } yield afaId

  def fromString(string: String): Option[AfaId] = {

    val UkIdPattern           = "^UK(\\d{4})(\\d{4})$".r
    val GbIdPattern           = "^GB(\\d{4})(\\d{3})$".r
    val GbIdPatternThreeDigit = "^GB(\\d{4})(\\d{2})$".r

    string match {
      case UkIdPattern(yearString, idString)           =>
        AfaId(yearString, idString, AfaId.UK).toOption
      case GbIdPattern(yearString, idString)           =>
        AfaId(yearString, idString, AfaId.GB(true)).toOption
      case GbIdPatternThreeDigit(yearString, idString) =>
        AfaId(yearString, idString, AfaId.GB(false)).toOption
      case _                                           =>
        None
    }
  }

  implicit lazy val pathBindable: PathBindable[AfaId] = new PathBindable[AfaId] {

    override def bind(key: String, value: String): Either[String, AfaId] =
      fromString(value) match {
        case Some(afaId) => Right(afaId)
        case None        => Left("Invalid AFA Id")
      }

    override def unbind(key: String, afaId: AfaId): String =
      afaId.toString
  }

  implicit lazy val reads: Reads[AfaId] = new Reads[AfaId] {

    override def reads(json: JsValue): JsResult[AfaId] = {

      val rawAfaId = json match {
        case string: JsString => string.value
        case obj              => (obj \ "value").as[JsString].value
      }

      fromString(string = rawAfaId) match {
        case Some(afaId) => JsSuccess(afaId)
        case None        => JsError("Afa Id not in format UKYYYYXXXX or GBYYYYXXX or GBYYYYXX")
      }
    }
  }

  implicit lazy val writes: Writes[AfaId] = Writes { afaId =>
    JsString(afaId.toString)
  }
}
