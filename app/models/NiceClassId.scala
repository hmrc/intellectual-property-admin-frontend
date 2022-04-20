/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

import scala.util.{Success, Try}

final case class NiceClassId private (value: Int) {

  require(value > 0 && value <= 45)

  override def toString: String = value.toString
}

object NiceClassId {

  def fromInt(value: Int): Option[NiceClassId] =
    if (value > 0 && value <= 45) {
      Some(NiceClassId(value))
    } else {
      None
    }

  def fromString(value: String): Option[NiceClassId] =
    Try(value.toInt) match {
      case Success(int) => fromInt(int)
      case _            => None
    }

  implicit lazy val reads: Reads[NiceClassId] = new Reads[NiceClassId] {

    override def reads(json: JsValue): JsResult[NiceClassId] = {

      val rawValue = json match {
        case JsNumber(number) if number.isValidInt => number.toIntExact.toString
        case JsString(value)                       => value
        case obj                                   => (obj \ "niceClass" ).as[JsString].value
      }

      fromString(rawValue) match {
        case Some(niceClass) => JsSuccess(niceClass)
        case None            => JsError("NICE class Id is not valid")
      }
    }
  }

  implicit lazy val writes: Writes[NiceClassId] = Writes {
    niceClassId => JsNumber(niceClassId.value)
  }
}
