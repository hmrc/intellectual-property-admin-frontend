/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package generators

import java.time.{Instant, LocalDate, ZoneOffset}

import models.afa._
import models.{AfaId, CompanyApplyingIsRightsHolder, InternationalAddress, NiceClassId, UkAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait AfaGenerators extends ModelGenerators {

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  implicit lazy val genCompanyUk: Gen[Company] =
      for {
        name           <- Gen.resize(200, Gen.alphaStr)
        acronym        <- Gen.option(Gen.resize(200, Gen.alphaStr))
        ukBased        <- arbitrary[Boolean]
        address        <- arbitrary[UkAddress]
        applicantType  <- arbitrary[CompanyApplyingIsRightsHolder]
      } yield Company(name, acronym, ukBased, address, applicantType)

  val genContactUk: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      otherPhone <- Gen.option(Gen.resize(200, Gen.alphaStr))
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[UkAddress]
    } yield Contact(company, name, phone, otherPhone, email, address)


  val genSecondaryTechnicalContactUk: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[UkAddress]
    } yield Contact(company, name, phone, None, email, address)

  val genSecondaryTechnicalContactInternational: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[InternationalAddress]
    } yield Contact(company, name, phone, None, email, address)

  val genLegalContactUk: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      otherPhone <- Gen.option(Gen.resize(200, Gen.alphaStr))
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[UkAddress]
    } yield Contact(company, name, phone, otherPhone, email, address)

  val genLegalContactInternational: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      otherPhone <- Gen.option(Gen.resize(200, Gen.alphaStr))
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[InternationalAddress]
    } yield Contact(company, name, phone, otherPhone, email, address)

  val genSecondaryLegalContactUk: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[UkAddress]
    } yield Contact(company, name, phone, None, email, address)

  val genSecondaryLegalContactInternational: Gen[Contact] =
    for {
      company    <- Gen.resize(200, Gen.alphaStr)
      name       <- Gen.resize(200, Gen.alphaStr)
      phone      <- Gen.resize(200, Gen.alphaStr)
      email      <- Gen.resize(200, Gen.alphaStr)
      address    <- arbitrary[InternationalAddress]
    } yield Contact(company, name, phone, None, email, address)

  val genRepresentativeContact: Gen[RepresentativeContact] =
    for {
      contactName           <- Gen.resize(200, Gen.alphaStr)
      company               <- Gen.resize(200, Gen.alphaStr)
      phone                 <- Gen.resize(200, Gen.alphaStr)
      email                 <- Gen.resize(200, Gen.alphaStr)
      roleOrPosition        <- Gen.option(Gen.resize(200, Gen.alphaStr))
      address               <- arbitrary[UkAddress]
    } yield RepresentativeContact(contactName, company, phone, email, roleOrPosition, address, Some(true))

  implicit lazy val arbitraryRepresentativeContact: Arbitrary[RepresentativeContact] =
    Arbitrary {
      for {
        contactName           <- arbitrary[String]
        company               <- Gen.resize(200, Gen.alphaStr)
        phone                 <- arbitrary[String]
        email                 <- arbitrary[String]
        roleOrPosition        <- Gen.option(arbitrary[String])
        address               <- Gen.oneOf(arbitrary[UkAddress], arbitrary[InternationalAddress])
        evidenceOfPowerToAct  <- arbitrary[Boolean]
      } yield RepresentativeContact(contactName, company, phone, email, roleOrPosition, address, Some(evidenceOfPowerToAct))
    }


  implicit lazy val arbitraryContact: Arbitrary[Contact] =
    Arbitrary {
      for {
        company    <- Gen.resize(200, Gen.alphaStr)
        name       <- Gen.resize(200, Gen.alphaStr)
        phone      <- Gen.resize(200, Gen.alphaStr)
        otherPhone <- Gen.option(Gen.resize(200, Gen.alphaStr))
        email      <- Gen.resize(200, Gen.alphaStr)
        address    <- Gen.oneOf(arbitrary[UkAddress], arbitrary[InternationalAddress])
      } yield Contact(company, name, phone, otherPhone, email, address)
    }

  implicit lazy val arbitraryExOfficio: Arbitrary[ExOfficio] =
    Arbitrary {
      for {
        wantsProtection <- arbitrary[Boolean]
      } yield ExOfficio(wantsProtection)
    }

  implicit lazy val arbitraryCompany: Arbitrary[Company] =
    Arbitrary {
      for {
        name           <- Gen.resize(200, Gen.alphaStr)
        acronym        <- Gen.option(Gen.resize(200, Gen.alphaStr))
        ukBased        <- arbitrary[Boolean]
        address        <- Gen.oneOf(arbitrary[UkAddress], arbitrary[InternationalAddress])
        applicantType  <- arbitrary[CompanyApplyingIsRightsHolder]
      } yield Company(name, acronym, ukBased, address, applicantType)
    }

  implicit lazy val arbitraryInitialAfa: Arbitrary[InitialAfa] =
    Arbitrary {
      for {
        id                          <- arbitraryUkAfaId()
        receiptDate                 <- Gen.option(datesBetween(LocalDate.now.minusYears(100), LocalDate.now.plusYears(100)))
        additionalInfoProvided     <- arbitrary[Boolean]
        shareWithEuropeanCommission <- arbitrary[Option[Boolean]]
        permission                  <- arbitrary[Boolean]
        exOfficio                   <- arbitrary[Option[ExOfficio]]
        applicant                   <- arbitrary[Company]
        representativeContact       <- arbitrary[RepresentativeContact]
        legalContact                <- arbitrary[Contact]
        technicalContact            <- arbitrary[Contact]
        endDate                     <- datesBetween(LocalDate.now.minusYears(100), LocalDate.now.plusYears(100))
        ipRights                    <- Gen.resize(10, Gen.nonEmptyListOf(arbitrary[IpRight]))
        restrictedHandling          <- arbitrary[Option[Boolean]]
        secondaryLegalContact       <- arbitrary[Option[Contact]]
        secondaryTechnicalContact    <- arbitrary[Option[Contact]]
      } yield InitialAfa(
        id, receiptDate, additionalInfoProvided,  shareWithEuropeanCommission, permission,
        exOfficio, applicant, legalContact, secondaryLegalContact, technicalContact, secondaryTechnicalContact,
        endDate, ipRights, representativeContact, restrictedHandling
      )
    }

  implicit lazy val arbitraryIpRight: Arbitrary[IpRight] =
    Arbitrary {
      Gen.oneOf[IpRight](
        arbitrary[Trademark],
        arbitrary[Copyright],
        arbitrary[Design],
        arbitrary[Patent],
        arbitrary[PlantVariety],
        arbitrary[GeographicalIndication],
        arbitrary[SupplementaryProtectionCertificate],
        arbitrary[SemiconductorTopography]
      )
    }

  implicit lazy val arbitraryCopyright: Arbitrary[Copyright] =
    Arbitrary {
      for {
        description <- arbitrary[String]
      } yield Copyright(description)
    }

  implicit lazy val arbitraryPlantVariety: Arbitrary[PlantVariety] =
    Arbitrary {
      for {
        description <- arbitrary[String]
      } yield PlantVariety(description)
    }

  implicit lazy val arbitrarySupplementaryProtectionCertificate: Arbitrary[SupplementaryProtectionCertificate] =
    Arbitrary {
      for {
        certificateType    <- arbitrary[String]
        registrationNumber <- arbitrary[String]
        registrationEnd    <- datesBetween(LocalDate.now, LocalDate.now.plusYears(100))
        description        <- arbitrary[String]
      } yield SupplementaryProtectionCertificate(certificateType, registrationNumber, registrationEnd, description)
    }

  implicit lazy val arbitrarySemiconductorTopography: Arbitrary[SemiconductorTopography] =
    Arbitrary {
      for {
        description <- arbitrary[String]
      } yield SemiconductorTopography(description)
    }

  implicit lazy val arbitraryTrademark: Arbitrary[Trademark] =
    Arbitrary {
      for {
        registrationNumber <- arbitrary[String]
        registrationEnd    <- datesBetween(LocalDate.now, LocalDate.now.plusYears(100))
        brand              <- arbitrary[Option[String]]
        description        <- arbitrary[String]
        niceClasses        <- Gen.nonEmptyListOf(arbitrary[NiceClassId])
      } yield Trademark(registrationNumber, registrationEnd, brand, description, niceClasses)
    }

  implicit lazy val arbitraryDesign: Arbitrary[Design] =
    Arbitrary {
      for {
        registrationNumber <- arbitrary[String]
        registrationEnd    <- datesBetween(LocalDate.now, LocalDate.now.plusYears(100))
        description        <- arbitrary[String]
      } yield Design(registrationNumber, registrationEnd, description)
    }

  implicit lazy val arbitraryPatent: Arbitrary[Patent] =
    Arbitrary {
      for {
        registrationNumber <- arbitrary[String]
        registrationEnd    <- datesBetween(LocalDate.now, LocalDate.now.plusYears(100))
        description        <- arbitrary[String]
      } yield Patent(registrationNumber, registrationEnd, description)
    }

  implicit lazy val arbitraryGeographicalIndication: Arbitrary[GeographicalIndication] =
    Arbitrary {
      for {
        description    <- arbitrary[String]
      } yield GeographicalIndication(description)
    }

  implicit lazy val arbitraryPublishedAfa: Arbitrary[PublishedAfa] =
    Arbitrary {
      for {
        id                          <- arbitrary[AfaId]
        receiptDate                 <- Gen.option(datesBetween(LocalDate.now.minusYears(100), LocalDate.now.plusYears(100)))
        additionalInfoProvided      <- arbitrary[Boolean]
        shareWithEuropeanCommission <- arbitrary[Option[Boolean]]
        permission                  <- arbitrary[Boolean]
        exOfficio                   <- arbitrary[Option[ExOfficio]]
        applicant                   <- arbitrary[Company]
        representativeContact       <- arbitrary[RepresentativeContact]
        legalContact                <- arbitrary[Contact]
        technicalContact            <- arbitrary[Contact]
        ipRights                    <- Gen.resize(10, Gen.nonEmptyListOf(arbitrary[IpRight]))
        endDate                     <- datesBetween(LocalDate.now(), LocalDate.now.plusYears(100))
        expirationDate              <- datesBetween(LocalDate.now(), LocalDate.now.plusYears(100))
        restrictedHandling          <- arbitrary[Option[Boolean]]
        secondaryLegalContact    <- arbitrary[Option[Contact]]
        secondaryTechnicalContact    <- arbitrary[Option[Contact]]
      } yield PublishedAfa(
        id, receiptDate, additionalInfoProvided, shareWithEuropeanCommission, permission, exOfficio, applicant,
        legalContact, secondaryLegalContact, technicalContact, secondaryTechnicalContact, ipRights, endDate,
        expirationDate, representativeContact, restrictedHandling
      )
    }
}
