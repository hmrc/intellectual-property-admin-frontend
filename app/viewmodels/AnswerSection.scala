/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package viewmodels

case class AnswerSection(headingKey: Option[String], rows: Seq[AnswerRow], sectionLink: Option[SectionLink] = None) extends Section
