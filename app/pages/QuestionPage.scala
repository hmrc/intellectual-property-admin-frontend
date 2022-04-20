/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import queries.{Gettable, Settable}

trait QuestionPage[A] extends Page with Gettable[A] with Settable[A] {

  def isRequired(answers: UserAnswers): Option[Boolean] = Some(true)
}
