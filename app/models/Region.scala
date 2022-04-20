/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._
import play.api.mvc.PathBindable

sealed trait Region

object Region {

  case object EnglandAndWales extends WithName("england-and-wales") with Region {
    implicit val writes: Writes[EnglandAndWales.type] = (_: EnglandAndWales.type) => JsString("england-and-wales")
  }

  case object Scotland extends WithName("scotland") with Region {
    implicit val writes: Writes[Scotland.type] = (_: Scotland.type) => JsString("scotland")
  }

  case object NorthernIreland extends WithName("northern-ireland") with Region {
    implicit val writes: Writes[NorthernIreland.type] = (_: NorthernIreland.type) => JsString("northern-ireland")
  }

  implicit lazy val pathBindable: PathBindable[Region] =
    new PathBindable[Region] {

      override def bind(key: String, value: String): Either[String, Region] =
        value match {
          case "england-and-wales" => Right(EnglandAndWales)
          case "scotland"          => Right(Scotland)
          case "northern-ireland"  => Right(NorthernIreland)
          case _                   => Left("Invalid bank holiday region")
        }

      override def unbind(key: String, value: Region): String =
        value match {
          case EnglandAndWales => "england-and-wales"
          case Scotland        => "scotland"
          case NorthernIreland => "northern-ireland"
        }
    }

  implicit lazy val reads: Reads[Region] = new Reads[Region] {
    override def reads(json: JsValue): JsResult[Region] = json match {
      case JsString("england-and-wales") => JsSuccess(EnglandAndWales)
      case JsString("scotland")          => JsSuccess(Scotland)
      case JsString("northern-ireland")  => JsSuccess(NorthernIreland)
      case _                             => JsError("Unable to read json as a Region")
    }
  }

  implicit lazy val writes: Writes[Region] = new Writes[Region] {
    override def writes(region: Region): JsValue = region match {
      case EnglandAndWales => JsString("england-and-wales")
      case Scotland        => JsString("scotland")
      case NorthernIreland => JsString("northern-ireland")
    }
  }
}
