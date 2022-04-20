/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.ApplicantLegalContact
import play.api.libs.json.JsPath

case object ApplicantLegalContactPage extends QuestionPage[ApplicantLegalContact] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "applicantLegalContact"

}
