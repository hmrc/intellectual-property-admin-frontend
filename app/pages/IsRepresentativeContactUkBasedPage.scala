/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsRepresentativeContactUkBasedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isRepresentativeContactUkBased"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  =>
        userAnswers.remove(RepresentativeContactInternationalAddressPage)
      case Some(false) =>
        userAnswers.remove(RepresentativeContactUkAddressPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
