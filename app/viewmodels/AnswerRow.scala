/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package viewmodels

import play.twirl.api.Html

abstract class AnswerRow

class AnswerRowWithUrl(val label: String, val answer: Html, val changeUrl: String) extends AnswerRow

case class IpRightAnswerRowWithUrl(
  override val label: String,
  override val answer: Html,
  override val changeUrl: String,
  ipRightIndex: Int
) extends AnswerRowWithUrl(label, answer, changeUrl)
case class CompanyAnswerRowWithUrl(
  override val label: String,
  override val answer: Html,
  override val changeUrl: String,
  companyText: String
) extends AnswerRowWithUrl(label, answer, changeUrl)
case class NiceClassAnswerRowWithUrl(
  override val label: String,
  override val answer: Html,
  override val changeUrl: String
) extends AnswerRowWithUrl(label, answer, changeUrl)

case class DisplayAnswerRow(label: String, answer: Html) extends AnswerRow
