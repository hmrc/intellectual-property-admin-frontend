/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import java.time.LocalDate

import play.api.libs.json.JsPath

object PublicationDeadlineQuery extends Gettable[LocalDate] with Settable[LocalDate] {

  override def path: JsPath = JsPath \ "publicationDeadline"
}
