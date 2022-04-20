/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsDescriptionWithBrand, IpRightsType, UserAnswers}
import play.api.libs.json.JsPath

final case class IpRightsDescriptionWithBrandPage(index: Int) extends QuestionPage[IpRightsDescriptionWithBrand] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "descriptionWithBrand"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers.get(IpRightsTypePage(index)) match {
      case Some(IpRightsType.Trademark) => Some(true)
      case Some(_)                      => Some(false)
      case None                         => None
    }
}
