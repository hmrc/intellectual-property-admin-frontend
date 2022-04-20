/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.afa

import play.api.libs.json.{Json, OFormat}

final case class ExOfficio(wantsOneYearProtection: Boolean)

object ExOfficio {

  implicit lazy val format: OFormat[ExOfficio] = Json.format[ExOfficio]
}
