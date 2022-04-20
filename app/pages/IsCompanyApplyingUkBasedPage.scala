/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsCompanyApplyingUkBasedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "applicantAddressUkBased"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  =>
        userAnswers.remove(CompanyApplyingInternationalAddressPage)
      case Some(false) =>
        userAnswers.remove(CompanyApplyingUkAddressPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
