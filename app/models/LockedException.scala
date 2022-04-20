/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, OFormat}

import scala.util.control.NoStackTrace

final case class LockedException(userId: String, name: String) extends Exception with NoStackTrace

object LockedException {

  implicit lazy val format: OFormat[LockedException] =
    Json.format[LockedException]
}
