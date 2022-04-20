/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.RepresentativeDetails
import play.api.libs.json.JsPath

case object RepresentativeDetailsPage extends QuestionPage[RepresentativeDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "representativeDetails"
}
