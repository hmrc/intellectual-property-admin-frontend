/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryIpRightsSupplementaryProtectionCertificateTypeUserAnswersEntry: Arbitrary[(IpRightsSupplementaryProtectionCertificateTypePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsSupplementaryProtectionCertificateTypePage]
        value <- arbitrary[IpRightsSupplementaryProtectionCertificateType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsNiceClassUserAnswersEntry: Arbitrary[(IpRightsNiceClassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsNiceClassPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsDescriptionWithBrandUserAnswersEntry: Arbitrary[(IpRightsDescriptionWithBrandPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsDescriptionWithBrandPage]
        value <- arbitrary[IpRightsDescriptionWithBrand].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsDescriptionUserAnswersEntry: Arbitrary[(IpRightsDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsDescriptionPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoIsTechnicalContactUserAnswersEntry: Arbitrary[(WhoIsTechnicalContactPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoIsTechnicalContactPage.type]
        value <- arbitrary[TechnicalContact].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryIsRepresentativeContactLegalContactUserAnswersEntry: Arbitrary[(IsRepresentativeContactLegalContactPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsRepresentativeContactLegalContactPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeDetailsUserAnswersEntry: Arbitrary[(RepresentativeDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RepresentativeDetailsPage.type]
        value <- arbitrary[RepresentativeDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryApplicantLegalContactUserAnswersEntry: Arbitrary[(ApplicantLegalContactPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ApplicantLegalContactPage.type]
        value <- arbitrary[ApplicantLegalContact].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCompanyApplyingIsRightsHolderUserAnswersEntry: Arbitrary[(CompanyApplyingIsRightsHolderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CompanyApplyingIsRightsHolderPage.type]
        value <- arbitrary[CompanyApplyingIsRightsHolder].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCompanyApplyingUserAnswersEntry: Arbitrary[(CompanyApplyingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CompanyApplyingPage.type]
        value <- arbitrary[CompanyApplying].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeleteDraftUserAnswersEntry: Arbitrary[(DeleteDraftPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeleteDraftPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUnlockAfaUserAnswersEntry: Arbitrary[(UnlockAfaPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UnlockAfaPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddIpRightUserAnswersEntry: Arbitrary[(AddIpRightPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddIpRightPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsAddGoodsUserAnswersEntry: Arbitrary[(IpRightsAddNiceClassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsAddNiceClassPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsRegistrationEndUserAnswersEntry: Arbitrary[(IpRightsRegistrationEndPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsRegistrationEndPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsRegistrationNumberUserAnswersEntry: Arbitrary[(IpRightsRegistrationNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsRegistrationNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPermissionToDestroySmallConsignmentsUserAnswersEntry: Arbitrary[(PermissionToDestroySmallConsignmentsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PermissionToDestroySmallConsignmentsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWantsOneYearRightsProtectionUserAnswersEntry: Arbitrary[(WantsOneYearRightsProtectionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WantsOneYearRightsProtectionPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIpRightsTypeUserAnswersEntry: Arbitrary[(IpRightsTypePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IpRightsTypePage]
        value <- arbitrary[IpRightsType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsExOfficioUserAnswersEntry: Arbitrary[(IsExOfficioPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsExOfficioPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEvidenceOfPowerToActUserAnswersEntry: Arbitrary[(EvidenceOfPowerToActPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EvidenceOfPowerToActPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTechnicalContactInternationalAddressUserAnswersEntry: Arbitrary[(TechnicalContactInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TechnicalContactInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRightsHolderContactInternationalAddressUserAnswersEntry: Arbitrary[(ApplicantLegalContactInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ApplicantLegalContactInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTechnicalContactUkAddressUserAnswersEntry: Arbitrary[(TechnicalContactUkAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TechnicalContactUkAddressPage.type]
        value <- arbitrary[UkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRightsHolderUkAddressUserAnswersEntry: Arbitrary[(ApplicantLegalContactUkAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ApplicantLegalContactUkAddressPage.type]
        value <- arbitrary[UkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsRightsHolderContactUkBasedUserAnswersEntry: Arbitrary[(IsApplicantLegalContactUkBasedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsApplicantLegalContactUkBasedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsTechnicalContactUkBasedUserAnswersEntry: Arbitrary[(IsTechnicalContactUkBasedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsTechnicalContactUkBasedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryApplicationReceiptDateUserAnswersEntry: Arbitrary[(ApplicationReceiptDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ApplicationReceiptDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }
}
