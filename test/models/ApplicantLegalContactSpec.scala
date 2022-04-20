/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ApplicantLegalContactSpec extends AnyWordSpec with Matchers {
  "Applicant legal contact" must {
    "create an object and convert it to technical contact" in {

      val name = "Joe Bloggs"
      val company = "companyName"
      val phone = "01010WOW"
      val otherPhone = Some("02020NO")
      val email = "him@there"
      val legal = new ApplicantLegalContact(company, name, phone, otherPhone, email)
      val tech = TechnicalContact(company, name, phone, email)
      legal.getAsTechnical mustEqual tech
    }

  }
}
