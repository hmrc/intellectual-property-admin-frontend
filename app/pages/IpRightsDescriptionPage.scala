/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, UserAnswers}
import play.api.libs.json.JsPath

final case class IpRightsDescriptionPage(index: Int) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "description"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers.get(IpRightsTypePage(index)) match {
      case Some(IpRightsType.Trademark) => Some(false)
      case Some(_)                      => Some(true)
      case None                         => None
    }
}
