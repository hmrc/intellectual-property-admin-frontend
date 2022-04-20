/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package viewmodels

case class RepeaterAnswerSection (headingKey: String,
                                  relevanceRow: AnswerRowWithUrl,
                                  rows: Seq[RepeaterAnswerRow],
                                  addLinkKey: String,
                                  addLinkUrl: String) extends Section
