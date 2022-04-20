/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package generators

import java.time.{LocalDate, Year}

import models.{WhoIsSecondaryLegalContact, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryIpRightsSupplementaryProtectionCertificateType: Arbitrary[IpRightsSupplementaryProtectionCertificateType] =
    Arbitrary {
      Gen.oneOf(IpRightsSupplementaryProtectionCertificateType.values)
    }

  implicit lazy val arbitraryIpRightsDescriptionWithBrand: Arbitrary[IpRightsDescriptionWithBrand] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
      } yield IpRightsDescriptionWithBrand(field1, field2)
    }

  implicit lazy val arbitraryTechnicalContact: Arbitrary[TechnicalContact] =
    Arbitrary {
      for {
        companyName  <- arbitrary[String]
        contactName  <- arbitrary[String]
        contactPhone <- arbitrary[String]
        contactEmail <- arbitrary[String]
      } yield TechnicalContact(companyName, contactName, contactPhone, contactEmail)
    }

  implicit lazy val arbitraryWhoIsSecondaryLegalContact: Arbitrary[WhoIsSecondaryLegalContact] =
    Arbitrary {
      for {
        companyName  <- arbitrary[String]
        contactName  <- arbitrary[String]
        contactPhone <- arbitrary[String]
        contactEmail <- arbitrary[String]
      } yield WhoIsSecondaryLegalContact(companyName, contactName, contactPhone, contactEmail)
    }

  implicit lazy val arbitraryApplicantLegalContact: Arbitrary[ApplicantLegalContact] =
    Arbitrary {
      for {
        companyName   <- arbitrary[String]
        name          <- arbitrary[String]
        telephone     <- arbitrary[String]
        otherPhone    <- Gen.option(arbitrary[String])
        email         <- arbitrary[String]
      } yield ApplicantLegalContact(companyName, name, telephone, otherPhone, email)
    }

  implicit lazy val arbitraryCompanyApplyingIsRightsHolder: Arbitrary[CompanyApplyingIsRightsHolder] =
    Arbitrary {
      Gen.oneOf(CompanyApplyingIsRightsHolder.values.toSeq)
    }

  implicit lazy val arbitraryRepresentativeDetails: Arbitrary[RepresentativeDetails] =
    Arbitrary {
      for {
        contactName       <- arbitrary[String]
        companyName       <- arbitrary[String]
        roleOrPosition    <- Gen.option(arbitrary[String])
        phone             <- arbitrary[String]
        email             <- arbitrary[String]
      } yield RepresentativeDetails(contactName, companyName, phone, email, roleOrPosition)
    }

  implicit lazy val arbitraryCompanyApplying: Arbitrary[CompanyApplying] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[Option[String]]
      } yield CompanyApplying(field1, field2)
    }

  implicit lazy val arbitraryAfaId: Arbitrary[AfaId] =
    Arbitrary(arbitraryAfaId(None))

  def arbitraryGbAfaIdThreeDigit(id: Option[Int] = None): Gen[AfaId] = {
    val min     = 0
    val max     = 999
    val minYear = LocalDate.now.getYear
    val maxYear = 2999

    val sequenceNumberGenerator: Gen[Int] = id.fold(Gen.chooseNum(min, max)) { x =>
      require((x >= min) && (x <= max))
      Gen.const(x)
    }

    for {
      year <- Gen.chooseNum(minYear, maxYear)
      id   <- sequenceNumberGenerator
    } yield AfaId(Year.parse(year.toString), id, prefix = AfaId.GB)
  }

  def arbitraryGbAfaIdTwoDigit(id: Option[Int] = None): Gen[AfaId] = {
    val min     = 0
    val max     = 99
    val minYear = LocalDate.now.getYear
    val maxYear = 2999

    val sequenceNumberGenerator: Gen[Int] = id.fold(Gen.chooseNum(min, max)) { x =>
      require((x >= min) && (x <= max))
      Gen.const(x)
    }

    for {
      year <- Gen.chooseNum(minYear, maxYear)
      id   <- sequenceNumberGenerator
    } yield AfaId(Year.parse(year.toString), id, prefix = AfaId.GB(false))
  }

  def arbitraryUkAfaId(id: Option[Int] = None): Gen[AfaId] = {
    val min     = 0
    val max     = 9999
    val minYear = LocalDate.now().getYear
    val maxYear = 2999

    val sequenceNumberGenerator: Gen[Int] = id.fold(Gen.chooseNum(min, max)) { x =>
      require((x >= min) && (x <= max))

      Gen.const(x)
    }

    for {
      year <- Gen.chooseNum(minYear, maxYear)
      id <- sequenceNumberGenerator
    } yield AfaId(Year.parse(year.toString), id, prefix = AfaId.UK)
  }

  def arbitraryGbAfaId(id: Option[Int] = None): Gen[AfaId] =
    Gen.oneOf(arbitraryGbAfaIdThreeDigit(id), arbitraryGbAfaIdTwoDigit(id))

  def arbitraryAfaId(id: Option[Int] = None): Gen[AfaId] =
    Gen.oneOf(arbitraryUkAfaId(id), arbitraryGbAfaId(id))

  implicit lazy val arbitraryIpRightsType: Arbitrary[IpRightsType] =
    Arbitrary {
      Gen.oneOf(IpRightsType.values)
    }

  implicit lazy val arbitraryInternationalAddress: Arbitrary[InternationalAddress] =
    Arbitrary {
      for {
        line1    <- arbitrary[String]
        line2    <- Gen.option(arbitrary[String])
        town     <- arbitrary[String]
        country  <- arbitrary[String]
        postCode <- Gen.option(arbitrary[String])
      } yield InternationalAddress(line1, line2, town, country, postCode)
    }

  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] =
    Arbitrary {
      for {
        line1    <- arbitrary[String]
        line2    <- Gen.option(arbitrary[String])
        town     <- arbitrary[String]
        county   <- Gen.option(arbitrary[String])
        postCode <- Gen.alphaStr
      } yield UkAddress(line1, line2, town, county, postCode)
    }

  implicit lazy val arbitraryNiceClassId: Arbitrary[NiceClassId] =
    Arbitrary {
      for {
        value <- Gen.choose(1, 45)
      } yield NiceClassId(value)
    }

  implicit lazy val arbitraryRegion: Arbitrary[Region] =
    Arbitrary{
      Gen.oneOf(Region.EnglandAndWales, Region.Scotland, Region.NorthernIreland)
    }

  implicit lazy val arbitraryContactOption: Arbitrary[ContactOptions] = {
    Arbitrary{
      Gen.oneOf(ContactOptions.values)
    }
  }
}
