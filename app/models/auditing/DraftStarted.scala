/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.auditing

import models.AfaId
import play.api.libs.json.{Json, OFormat}

final case class DraftStarted(id: AfaId, userName: String, PID: String)

object DraftStarted {

  implicit lazy val format: OFormat[DraftStarted] = Json.format[DraftStarted]
}
