/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.requests

import play.api.mvc.{Request, WrappedRequest}
import models.UserAnswers

case class OptionalDataRequest[A] (request: Request[A], internalId: String, name: String, userAnswers: Option[UserAnswers]) extends WrappedRequest[A](request)

case class DataRequest[A] (request: Request[A], internalId: String, name: String, userAnswers: UserAnswers) extends WrappedRequest[A](request)
