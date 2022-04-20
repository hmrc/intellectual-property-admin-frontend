/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package viewmodels

import play.twirl.api.Html

abstract class AnswerRow

class AnswerRowWithUrl(val label: String, val answer: Html, val changeUrl: String) extends AnswerRow

case class IpRightAnswerRowWithUrl(override val label: String, override val answer: Html,
                                   override val changeUrl: String, ipRightIndex: Int) extends
  AnswerRowWithUrl(label,answer, changeUrl)
case class CompanyAnswerRowWithUrl(override val label: String, override val answer: Html,
                                   override val changeUrl: String, companyText: String) extends
  AnswerRowWithUrl(label, answer, changeUrl)
case class NiceClassAnswerRowWithUrl(override val label: String, override val answer: Html, override val changeUrl: String) extends
  AnswerRowWithUrl(label, answer, changeUrl)

case class DisplayAnswerRow(label: String, answer: Html) extends AnswerRow
