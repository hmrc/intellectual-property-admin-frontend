/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package viewmodels

import play.api.mvc.Call

case class ReviewRow(name: String, deleteUrl: Option[Call], changeUrl: Call)
