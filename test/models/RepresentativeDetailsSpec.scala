/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RepresentativeDetailsSpec extends AnyWordSpec with Matchers {
  "Representative Details" must {
    "create an object and convert it to legal contact" in {

      val name = "Joe Bloggs"
      val company = "companyName"
      val phone = "01010WOW"
      val email = "him@there"
      val role = Some("CEO")
      val rep = new RepresentativeDetails(name, company, phone, email, role)
      val legal = new ApplicantLegalContact(company, name, phone, None, email)
      rep.getAsLegal mustEqual legal
    }

  }
}
