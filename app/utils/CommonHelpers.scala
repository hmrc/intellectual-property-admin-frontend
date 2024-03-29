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

package utils

import models.requests.DataRequest
import pages.CompanyApplyingPage
import play.api.i18n.Messages
import play.api.mvc.AnyContent

object CommonHelpers {

  val rejectXssChars: String = """^[^<>"&]*$"""
  val regexErrorKey: String  = "regex.error"

  val regexXSSNoAmpersand: String    = """^[^<>"]*$"""
  val errorKeyXSSNoAmpersand: String = "error.regexXSSNoAmpersand"

  val telephoneRegex = """^\+[0-9]{1,19}$|^00[0-9]{1,19}|^0[0-9]{9,10}$"""

  def getApplicantName[A](block: String => A)(implicit request: DataRequest[AnyContent], messages: Messages): A =
    block {
      request.userAnswers
        .get(CompanyApplyingPage)
        .map(company => company.acronym.getOrElse(company.name))
        .fold(Messages("companyApplying.unknownApplicant"))(identity)
    }
}
