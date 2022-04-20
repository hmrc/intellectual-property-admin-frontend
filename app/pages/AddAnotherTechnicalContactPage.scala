/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object AddAnotherTechnicalContactPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addAnotherTechnicalContact"

    override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
      value match {
        case Some(false) =>
          for {
            first  <- userAnswers.remove(WhoIsSecondaryTechnicalContactPage)
            second <- first.remove(IsSecondaryTechnicalContactUkBasedPage)
            third  <- second.remove(SecondaryTechnicalContactUkAddressPage)
            fourth  <- third.remove(SecondaryTechnicalContactInternationalAddressPage)
          } yield fourth

        case _ =>
          super.cleanup(value, userAnswers)
      }
}
