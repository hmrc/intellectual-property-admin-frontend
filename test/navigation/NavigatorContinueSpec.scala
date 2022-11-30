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

package navigation

import controllers.routes
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import queries.AllIpRightsQuery

import java.time.LocalDate

class NavigatorContinueSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with TryValues
    with ScalaCheckPropertyChecks
    with Generators
    with OptionValues {

  val navigator: Navigator = new Navigator

  private val afaId: AfaId                                      = arbitraryUkAfaId().sample.value
  private val receiptDate: LocalDate                            = datesBetween(LocalDate.now.minusDays(100), LocalDate.now).sample.value
  private val companyApplying                                   = arbitrary[CompanyApplying].sample.value
  private val representativeDetails                             = arbitrary[RepresentativeDetails].sample.value
  private val applicantLegalContact                             = arbitrary[ApplicantLegalContact].sample.value
  private val ukAddress                                         = arbitrary[UkAddress].sample.value
  private val technicalContact                                  = arbitrary[TechnicalContact].sample.value
  private val secondaryLegalContact: WhoIsSecondaryLegalContact = arbitrary[WhoIsSecondaryLegalContact].sample.value
  private val secondaryTechnicalContact                         = arbitrary[TechnicalContact].sample.value

  private val description = arbitrary[String].sample.value

  private val descriptionWithBrand = arbitrary[IpRightsDescriptionWithBrand].sample.value
  private val certificateType      = arbitrary[IpRightsSupplementaryProtectionCertificateType].sample.value
  private val registrationNumber   = arbitrary[String].sample.value
  private val registrationEnd      = datesBetween(LocalDate.now.plusDays(1), LocalDate.now.plusYears(10)).sample.value
  private val niceClass            = arbitrary[NiceClassId].sample.value
  private val shareWithEC          = arbitrary[Boolean].sample.value

  private val afaWithoutRights =
    new UserAnswers(afaId)
      .set(ApplicationReceiptDatePage, receiptDate)
      .success
      .value
      .set(AdditionalInfoProvidedPage, true)
      .success
      .value
      .set(ShareWithEuropeanCommissionPage, shareWithEC)
      .success
      .value
      .set(PermissionToDestroySmallConsignmentsPage, true)
      .success
      .value
      .set(IsExOfficioPage, true)
      .success
      .value
      .set(WantsOneYearRightsProtectionPage, true)
      .success
      .value
      .set(CompanyApplyingPage, companyApplying)
      .success
      .value
      .set(IsCompanyApplyingUkBasedPage, true)
      .success
      .value
      .set(CompanyApplyingUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
      .success
      .value
      .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.RightsHolder)
      .success
      .value
      .set(RepresentativeDetailsPage, representativeDetails)
      .success
      .value
      .set(IsRepresentativeContactUkBasedPage, true)
      .success
      .value
      .set(RepresentativeContactUkAddressPage, ukAddress)
      .success
      .value
      .set(IsRepresentativeContactLegalContactPage, false)
      .success
      .value
      .set(ApplicantLegalContactPage, applicantLegalContact)
      .success
      .value
      .set(IsApplicantLegalContactUkBasedPage, true)
      .success
      .value
      .set(ApplicantLegalContactUkAddressPage, ukAddress)
      .success
      .value
      .set(RepresentativeDetailsPage, representativeDetails)
      .success
      .value
      .set(EvidenceOfPowerToActPage, true)
      .success
      .value
      .set(RestrictedHandlingPage, !shareWithEC)
      .success
      .value
      .set(AddAnotherLegalContactPage, true)
      .success
      .value
      .set(WhoIsSecondaryLegalContactPage, secondaryLegalContact)
      .success
      .value
      .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
      .success
      .value
      .set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress)
      .success
      .value
      .set(SelectTechnicalContactPage, ContactOptions.SomeoneElse)
      .success
      .value
      .set(WhoIsTechnicalContactPage, technicalContact)
      .success
      .value
      .set(IsTechnicalContactUkBasedPage, true)
      .success
      .value
      .set(TechnicalContactUkAddressPage, ukAddress)
      .success
      .value
      .set(AddAnotherTechnicalContactPage, true)
      .success
      .value
      .set(SelectOtherTechnicalContactPage, ContactOptions.SomeoneElse)
      .success
      .value
      .set(WhoIsSecondaryTechnicalContactPage, secondaryTechnicalContact)
      .success
      .value
      .set(IsSecondaryTechnicalContactUkBasedPage, true)
      .success
      .value
      .set(SecondaryTechnicalContactUkAddressPage, ukAddress)
      .success
      .value

  private val completeAfa =
    afaWithoutRights
      .set(IpRightsTypePage(0), IpRightsType.Copyright)
      .success
      .value
      .set(IpRightsDescriptionPage(0), description)
      .success
      .value
      .set(IpRightsTypePage(1), IpRightsType.Trademark)
      .success
      .value
      .set(IpRightsRegistrationNumberPage(1), registrationNumber)
      .success
      .value
      .set(IpRightsRegistrationEndPage(1), registrationEnd)
      .success
      .value
      .set(IpRightsDescriptionWithBrandPage(1), descriptionWithBrand)
      .success
      .value
      .set(IpRightsNiceClassPage(1, 0), niceClass)
      .success
      .value
      .set(IpRightsTypePage(2), IpRightsType.Design)
      .success
      .value
      .set(IpRightsRegistrationNumberPage(2), registrationNumber)
      .success
      .value
      .set(IpRightsRegistrationEndPage(2), registrationEnd)
      .success
      .value
      .set(IpRightsDescriptionPage(2), description)
      .success
      .value
      .set(IpRightsTypePage(3), IpRightsType.Patent)
      .success
      .value
      .set(IpRightsRegistrationNumberPage(3), registrationNumber)
      .success
      .value
      .set(IpRightsRegistrationEndPage(3), registrationEnd)
      .success
      .value
      .set(IpRightsDescriptionPage(3), description)
      .success
      .value
      .set(IpRightsTypePage(4), IpRightsType.PlantVariety)
      .success
      .value
      .set(IpRightsDescriptionPage(4), description)
      .success
      .value
      .set(IpRightsTypePage(5), IpRightsType.GeographicalIndication)
      .success
      .value
      .set(IpRightsDescriptionPage(5), description)
      .success
      .value
      .set(IpRightsTypePage(6), IpRightsType.SemiconductorTopography)
      .success
      .value
      .set(IpRightsDescriptionPage(6), description)
      .success
      .value
      .set(IpRightsTypePage(7), IpRightsType.SupplementaryProtectionCertificate)
      .success
      .value
      .set(IpRightsSupplementaryProtectionCertificateTypePage(7), certificateType)
      .success
      .value
      .set(IpRightsRegistrationNumberPage(7), registrationNumber)
      .success
      .value
      .set(IpRightsRegistrationEndPage(7), registrationEnd)
      .success
      .value
      .set(IpRightsDescriptionPage(7), description)
      .success
      .value

  ".continue" - {

    "must go to Application Receipt Date" - {

      "when this is not a migrated AFA and Application Receipt Date is not answered" in {

        val answers = completeAfa.remove(ApplicationReceiptDatePage).success.value

        navigator.continue(answers) mustEqual routes.ApplicationReceiptDateController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Additional Info Provided" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(AdditionalInfoProvidedPage).success.value

        navigator.continue(answers) mustEqual routes.AdditionalInfoProvidedController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Permission to Destroy Small Consignments" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(PermissionToDestroySmallConsignmentsPage).success.value

        navigator.continue(answers) mustEqual routes.PermissionToDestroySmallConsignmentsController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Is Ex-officio" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IsExOfficioPage).success.value

        navigator.continue(answers) mustEqual routes.IsExOfficioController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Wants One Year Protection" - {

      "when Is Ex-officio is `true` and this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsExOfficioPage, true)
            .success
            .value
            .remove(WantsOneYearRightsProtectionPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.WantsOneYearRightsProtectionController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Company Applying" - {

      "when Is Ex-officio is `true` and this is the first unanswered question" in {

        val answers = completeAfa.remove(CompanyApplyingPage).success.value

        navigator.continue(answers) mustEqual routes.CompanyApplyingController.onPageLoad(NormalMode, answers.id)
      }

      "when Is Ex-officio is `false`, Wants One Year Protection is unanswered and this question is unanswered" in {

        val answers =
          completeAfa
            .set(IsExOfficioPage, false)
            .success
            .value
            .remove(WantsOneYearRightsProtectionPage)
            .success
            .value
            .remove(CompanyApplyingPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.CompanyApplyingController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Company Applying Is Rights Holder" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(CompanyApplyingIsRightsHolderPage).success.value

        navigator.continue(answers) mustEqual routes.CompanyApplyingIsRightsHolderController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Representative Details" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(RepresentativeDetailsPage).success.value

        navigator.continue(answers) mustEqual routes.RepresentativeContactController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Is Representative Contact UK Based" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IsRepresentativeContactUkBasedPage).success.value

        navigator.continue(answers) mustEqual routes.IsRepresentativeContactUkBasedController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Representative Contact UK Address" - {

      "when Is Representative Contact UK Based is `true` and Representative Contact UK Address is not answered" in {

        val answers =
          completeAfa
            .set(IsRepresentativeContactUkBasedPage, true)
            .success
            .value
            .remove(RepresentativeContactUkAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.RepresentativeContactUkAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Representative Contact International Address" - {

      "when Is Representative Contact UK Based is `false` and Representative Contact International Address is not answered" in {

        val answers =
          completeAfa
            .set(IsRepresentativeContactUkBasedPage, false)
            .success
            .value
            .remove(RepresentativeContactInternationalAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.RepresentativeContactInternationalAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Who Is Legal Contact" - {

      "when Is Representative Contact Legal Contact is `false` and this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsRepresentativeContactLegalContactPage, false)
            .success
            .value
            .remove(IsRepresentativeContactLegalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.IsRepresentativeContactLegalContactController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Applicant Legal Contact" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(ApplicantLegalContactPage).success.value

        navigator.continue(answers) mustEqual routes.ApplicantLegalContactController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Is Applicant Legal Contact UK Based" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IsApplicantLegalContactUkBasedPage).success.value

        navigator.continue(answers) mustEqual routes.IsApplicantLegalContactUkBasedController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Applicant Legal Contact UK Address" - {

      "when that is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsApplicantLegalContactUkBasedPage, true)
            .success
            .value
            .remove(ApplicantLegalContactUkAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.ApplicantLegalContactUkAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Applicant Legal Contact International Address" - {

      "when that is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsApplicantLegalContactUkBasedPage, false)
            .success
            .value

        navigator.continue(answers) mustEqual routes.ApplicantLegalContactInternationalAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Add another Legal Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(AddAnotherLegalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.AddAnotherLegalContactController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Select secondary legal Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(WhoIsSecondaryLegalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.WhoIsSecondaryLegalContactController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Select secondary legal Contact is uk based" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(IsApplicantSecondaryLegalContactUkBasedPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.IsApplicantSecondaryLegalContactUkBasedController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the applicant secondary Legal Contact UK Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(ApplicantSecondaryLegalContactUkAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.ApplicantSecondaryLegalContactUkAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the applicant secondary Legal Contact International Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsApplicantSecondaryLegalContactUkBasedPage, false)
            .success
            .value

        navigator.continue(answers) mustEqual routes.ApplicantSecondaryLegalContactInternationalAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Select Technical Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(SelectTechnicalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.SelectTechnicalContactController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Who Is Technical Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(WhoIsTechnicalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.WhoIsTechnicalContactController.onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Is Technical Contact UK Based" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(IsTechnicalContactUkBasedPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.IsTechnicalContactUkBasedController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Technical Contact UK Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsTechnicalContactUkBasedPage, true)
            .success
            .value
            .remove(TechnicalContactUkAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.TechnicalContactUkAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Technical Contact International Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsTechnicalContactUkBasedPage, false)
            .success
            .value
            .remove(TechnicalContactInternationalAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.TechnicalContactInternationalAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Add Another Technical Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(AddAnotherTechnicalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.AddAnotherTechnicalContactController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Select other Technical Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(SelectOtherTechnicalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.SelectOtherTechnicalContactController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Who Is Secondary Technical Contact" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(WhoIsSecondaryTechnicalContactPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.WhoIsSecondaryTechnicalContactController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Is Secondary Technical Contact UK Based" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(IsSecondaryTechnicalContactUkBasedPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.IsSecondaryTechnicalContactUkBasedController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the Secondary Technical Contact UK Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsSecondaryTechnicalContactUkBasedPage, true)
            .success
            .value
            .remove(SecondaryTechnicalContactUkAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.SecondaryTechnicalContactUkAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to the SecondaryTechnical Contact International Address" - {

      "when this is the first unanswered question" in {

        val answers =
          completeAfa
            .set(IsSecondaryTechnicalContactUkBasedPage, false)
            .success
            .value
            .remove(SecondaryTechnicalContactInternationalAddressPage)
            .success
            .value

        navigator.continue(answers) mustEqual routes.SecondaryTechnicalContactInternationalAddressController
          .onPageLoad(NormalMode, answers.id)
      }
    }

    "must go to Ip Rights Type for index 0" - {

      "when there are no IP rights" in {

        navigator.continue(afaWithoutRights) mustEqual routes.IpRightsTypeController
          .onPageLoad(NormalMode, 0, afaWithoutRights.id)
      }

      "when IP rights is empty" in {
        val x = afaWithoutRights
          .set(AllIpRightsQuery, List.empty)
          .success
          .value

        navigator.continue(x) mustEqual routes.IpRightsTypeController.onPageLoad(NormalMode, 0, afaWithoutRights.id)
      }
    }

    "must go to Description for a Copyright" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(0)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 0, answers.id)
      }
    }

    "must go to Registration Number for a Trademark" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationNumberPage(1)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationNumberController
          .onPageLoad(NormalMode, 1, answers.id)
      }
    }

    "must go to Registration End for a Trademark" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationEndPage(1)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationEndController
          .onPageLoad(NormalMode, 1, answers.id)
      }
    }

    "must go to Description with Brand for a Trademark" - {

      "when that is the first unanswered question" in {

        val answers =
          completeAfa
            .remove(IpRightsDescriptionWithBrandPage(1))
            .success
            .value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionWithBrandController
          .onPageLoad(NormalMode, 1, answers.id)
      }
    }

    "must go to NICE class index 0 for a Trademark" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(pages.IpRightsNiceClassPage(1, 0)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsNiceClassController
          .onPageLoad(NormalMode, 1, 0, answers.id)
      }
    }

    "must go to Registration Number for a Design" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationNumberPage(2)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationNumberController
          .onPageLoad(NormalMode, 2, answers.id)
      }
    }

    "must go to Registration End for a Design" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationEndPage(2)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationEndController
          .onPageLoad(NormalMode, 2, answers.id)
      }
    }

    "must go to Description for a Design" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(2)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 2, answers.id)
      }
    }

    "must go to Registration Number for a Patent" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationNumberPage(3)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationNumberController
          .onPageLoad(NormalMode, 3, answers.id)
      }
    }

    "must go to Registration End for a Patent" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationEndPage(3)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationEndController
          .onPageLoad(NormalMode, 3, answers.id)
      }
    }

    "must go to Description for a Patent" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(3)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 3, answers.id)
      }
    }

    "must go to Description for a Plant Variety" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(4)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 4, answers.id)
      }
    }

    "must go to Description for a Geographical Indication" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(5)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 5, answers.id)
      }
    }

    "must go to Description for a Semiconductor Topography" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(6)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 6, answers.id)
      }
    }

    "must go to Certificate Type for a Supplementary Protection Certificate" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsSupplementaryProtectionCertificateTypePage(7)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsSupplementaryProtectionCertificateTypeController
          .onPageLoad(NormalMode, 7, answers.id)
      }
    }

    "must go to Registration Number for a Supplementary Protection Certificate" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationNumberPage(7)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationNumberController
          .onPageLoad(NormalMode, 7, answers.id)
      }
    }

    "must go to Registration End for a Supplementary Protection Certificate" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsRegistrationEndPage(7)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsRegistrationEndController
          .onPageLoad(NormalMode, 7, answers.id)
      }
    }

    "must go to Description for a Supplementary Protection Certificate" - {

      "when that is the first unanswered question" in {

        val answers = completeAfa.remove(IpRightsDescriptionPage(7)).success.value

        navigator.continue(answers) mustEqual routes.IpRightsDescriptionController.onPageLoad(NormalMode, 7, answers.id)
      }
    }
  }

  "must go to the restricted handling page" - {
    "when that is the first unanswered question" in {

      val answers = completeAfa.remove(RestrictedHandlingPage).success.value

      navigator.continue(answers) mustEqual routes.RestrictedHandlingController.onPageLoad(NormalMode, answers.id)
    }
  }
}
