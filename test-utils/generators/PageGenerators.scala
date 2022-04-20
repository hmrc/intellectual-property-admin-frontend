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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryIpRightsSupplementaryProtectionCertificateTypePage: Arbitrary[IpRightsSupplementaryProtectionCertificateTypePage] =
    Arbitrary(IpRightsSupplementaryProtectionCertificateTypePage(0))

  implicit lazy val arbitraryIpRightsNiceClassPage: Arbitrary[IpRightsNiceClassPage] =
    Arbitrary(IpRightsNiceClassPage(0, 0))

  implicit lazy val arbitraryIpRightsDescriptionWithBrandPage: Arbitrary[IpRightsDescriptionWithBrandPage] =
    Arbitrary(IpRightsDescriptionWithBrandPage(0))

  implicit lazy val arbitraryIpRightsDescriptionPage: Arbitrary[IpRightsDescriptionPage] =
    Arbitrary(IpRightsDescriptionPage(0))

  implicit lazy val arbitraryWhoIsTechnicalContactPage: Arbitrary[WhoIsTechnicalContactPage.type] =
    Arbitrary(WhoIsTechnicalContactPage)

  implicit lazy val arbitraryWhoIsSecondaryTechnicalContactPage: Arbitrary[WhoIsSecondaryTechnicalContactPage.type] =
    Arbitrary(WhoIsSecondaryTechnicalContactPage)

  implicit lazy val arbitraryIsRepresentativeContactLegalContactPage: Arbitrary[IsRepresentativeContactLegalContactPage.type] =
    Arbitrary(IsRepresentativeContactLegalContactPage)

  implicit lazy val arbitraryRepresentativeContactPage: Arbitrary[RepresentativeDetailsPage.type] =
    Arbitrary(RepresentativeDetailsPage)

  implicit lazy val arbitraryApplicantLegalContactPage: Arbitrary[ApplicantLegalContactPage.type] =
    Arbitrary(ApplicantLegalContactPage)

  implicit lazy val arbitraryCompanyApplyingIsRightsHolderPage: Arbitrary[CompanyApplyingIsRightsHolderPage.type] =
    Arbitrary(CompanyApplyingIsRightsHolderPage)

  implicit lazy val arbitraryCompanyApplyingPage: Arbitrary[CompanyApplyingPage.type] =
    Arbitrary(CompanyApplyingPage)

  implicit lazy val arbitraryDeleteDraftPage: Arbitrary[DeleteDraftPage.type] =
    Arbitrary(DeleteDraftPage)

  implicit lazy val arbitraryUnlockAfaPage: Arbitrary[UnlockAfaPage.type] =
    Arbitrary(UnlockAfaPage)

  implicit lazy val arbitraryAddIpRightPage: Arbitrary[AddIpRightPage.type] =
    Arbitrary(AddIpRightPage)

  implicit lazy val arbitraryIpRightsAddGoodsPage: Arbitrary[IpRightsAddNiceClassPage] =
    Arbitrary(IpRightsAddNiceClassPage(0))

  implicit lazy val arbitraryIpRightsRegistrationEndPage: Arbitrary[IpRightsRegistrationEndPage] =
    Arbitrary(IpRightsRegistrationEndPage(0))

  implicit lazy val arbitraryIpRightsRegistrationNumberPage: Arbitrary[IpRightsRegistrationNumberPage] =
    Arbitrary(IpRightsRegistrationNumberPage(0))

  implicit lazy val arbitraryPermissionToDestroySmallConsignmentsPage: Arbitrary[PermissionToDestroySmallConsignmentsPage.type] =
    Arbitrary(PermissionToDestroySmallConsignmentsPage)

  implicit lazy val arbitraryWantsOneYearRightsProtectionPage: Arbitrary[WantsOneYearRightsProtectionPage.type] =
    Arbitrary(WantsOneYearRightsProtectionPage)

  implicit lazy val arbitraryIpRightsTypePage: Arbitrary[IpRightsTypePage] =
    Arbitrary(IpRightsTypePage(0))

  implicit lazy val arbitraryIsExOfficioPage: Arbitrary[IsExOfficioPage.type] =
    Arbitrary(IsExOfficioPage)

  implicit lazy val arbitraryEvidenceOfPowerToActPage: Arbitrary[EvidenceOfPowerToActPage.type] =
    Arbitrary(EvidenceOfPowerToActPage)

  implicit lazy val arbitraryTechnicalContactInternationalAddressPage: Arbitrary[TechnicalContactInternationalAddressPage.type] =
    Arbitrary(TechnicalContactInternationalAddressPage)

  implicit lazy val arbitraryRightsHolderContactInternationalAddressPage: Arbitrary[ApplicantLegalContactInternationalAddressPage.type] =
    Arbitrary(ApplicantLegalContactInternationalAddressPage)

  implicit lazy val arbitraryTechnicalContactUkAddressPage: Arbitrary[TechnicalContactUkAddressPage.type] =
    Arbitrary(TechnicalContactUkAddressPage)

  implicit lazy val arbitraryRightsHolderUkAddressPage: Arbitrary[ApplicantLegalContactUkAddressPage.type] =
    Arbitrary(ApplicantLegalContactUkAddressPage)

  implicit lazy val arbitraryIsRightsHolderContactUkBasedPage: Arbitrary[IsApplicantLegalContactUkBasedPage.type] =
    Arbitrary(IsApplicantLegalContactUkBasedPage)

  implicit lazy val arbitraryIsTechnicalContactUkBasedPage: Arbitrary[IsTechnicalContactUkBasedPage.type] =
    Arbitrary(IsTechnicalContactUkBasedPage)

  implicit lazy val arbitraryApplicationReceiptDatePage: Arbitrary[ApplicationReceiptDatePage.type] =
    Arbitrary(ApplicationReceiptDatePage)
}
