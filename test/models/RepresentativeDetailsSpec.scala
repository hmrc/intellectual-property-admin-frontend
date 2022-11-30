/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RepresentativeDetailsSpec extends AnyWordSpec with Matchers {
  "Representative Details" must {
    "create an object and convert it to legal contact" in {

      val name    = "Joe Bloggs"
      val company = "companyName"
      val phone   = "01010WOW"
      val email   = "him@there"
      val role    = Some("CEO")
      val rep     = new RepresentativeDetails(name, company, phone, email, role)
      val legal   = new ApplicantLegalContact(company, name, phone, None, email)
      rep.getAsLegal mustEqual legal
    }

  }
}
