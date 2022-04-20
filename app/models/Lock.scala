/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Json, OFormat}

final case class Lock(_id: AfaId, userId: String, name: String)

object Lock {

  implicit lazy val format: OFormat[Lock] = Json.format[Lock]
}
