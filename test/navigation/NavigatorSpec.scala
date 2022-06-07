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

import constants.ContactsConstants._
import base.SpecBase
import controllers.routes
import generators.Generators
import models.{WhoIsSecondaryLegalContact, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.TryValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import queries.{IprDetailsQuery, NiceClassIdsQuery, RemoveIprQuery, RemoveNiceClassQuery}

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TryValues {

  val navigator = new Navigator

  val afaId: AfaId = userAnswersId
  val arbitrarilyHighIndex = 100

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers(afaId)) mustBe controllers.routes.CreateAfaIdController.onPageLoad
      }

      // Application section
      "go from Application Receipt Date to Company Applying page" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(ApplicationReceiptDatePage, NormalMode, userAnswers)
              .mustBe(routes.CompanyApplyingController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "go from Company Applying to is Company Applying Uk based" in {

        forAll(arbitrary[UserAnswers], arbitrary[CompanyApplying]) {
          (userAnswers, answer) =>

            val answers = userAnswers.set(CompanyApplyingPage, answer).success.value

            navigator.nextPage(CompanyApplyingPage, NormalMode, answers)
              .mustBe(routes.IsCompanyApplyingUkBasedController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "when going from is Company Applying Uk Based" when {

        "user answers 'yes' must go to Company Applying Uk Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsCompanyApplyingUkBasedPage, true).success.value

              navigator.nextPage(IsCompanyApplyingUkBasedPage, NormalMode, answers)
                .mustBe(routes.CompanyApplyingUkAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers 'no' must go to Company Applying International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsCompanyApplyingUkBasedPage, false).success.value

              navigator.nextPage(IsCompanyApplyingUkBasedPage, NormalMode, answers)
                .mustBe(routes.CompanyApplyingInternationalAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Company Applying Uk Address to Company Applying Is Rights Holder" in {
        forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
          (userAnswers, ukAddress) =>

            val answers = userAnswers.set(CompanyApplyingUkAddressPage, ukAddress).success.value

            navigator.nextPage(CompanyApplyingUkAddressPage, NormalMode, answers)
              .mustBe(routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, answers.id))
        }
      }

      "go from Company Applying International Address to Company Applying Is Rights Holder" in {

        forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
          (userAnswers, address) =>

            val answers = userAnswers.set(CompanyApplyingInternationalAddressPage, address).success.value

            navigator.nextPage(CompanyApplyingInternationalAddressPage, NormalMode, answers)
              .mustBe(routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, answers.id))
        }
      }

      "go from Company Applying Is Rights Holder to Representative contact page" in {
        forAll(arbitrary[UserAnswers], arbitrary[CompanyApplyingIsRightsHolder]) {
          (userAnswers, answer) =>

            val answers = userAnswers.set(CompanyApplyingIsRightsHolderPage, answer).success.value

            navigator.nextPage(CompanyApplyingIsRightsHolderPage, NormalMode, answers)
              .mustBe(routes.RepresentativeContactController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      //Representative contact section

      "go from representative contact page to Power TO Act page" in {
        forAll(arbitrary[UserAnswers], arbitrary[RepresentativeDetails]) {
          (userAnswers, answer) =>

            val answers = userAnswers.set(RepresentativeDetailsPage, answer).success.value

            navigator.nextPage(RepresentativeDetailsPage, NormalMode, answers)
              .mustBe(routes.EvidenceOfPowerToActController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "go from evidence of power to act page to Representative Based page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(EvidenceOfPowerToActPage, NormalMode, userAnswers)
              .mustBe(routes.IsRepresentativeContactUkBasedController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "when going from Representative Contact Uk Based" when {

        "user answers 'yes' must go to Representative Contact Uk Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, true).success.value

              navigator.nextPage(IsRepresentativeContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.RepresentativeContactUkAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers 'no' must go to Representative Contact International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, false).success.value

              navigator.nextPage(IsRepresentativeContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.RepresentativeContactInternationalAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Representative Contact Uk Address to Is Representative Contact Legal Contact page" in {
        forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
          (userAnswers, ukAddress) =>

            val answers = userAnswers.set(RepresentativeContactUkAddressPage, ukAddress).success.value

            navigator.nextPage(RepresentativeContactUkAddressPage, NormalMode, answers)
              .mustBe(routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, answers.id))
        }
      }

      "go from Representative Contact International Address to Is Representative Contact Legal Contact page" in {

        forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
          (userAnswers, address) =>

            val answers = userAnswers.set(RepresentativeContactInternationalAddressPage, address).success.value

            navigator.nextPage(RepresentativeContactInternationalAddressPage, NormalMode, answers)
              .mustBe(routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, answers.id))
        }
      }


      ////////////////////////////////

      // Legal contact section

      "when going from Is Representative Contact Legal Contact" when {
        "user answers No must go to Who Is Legal Contact" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(IsRepresentativeContactLegalContactPage, false).success.value

              navigator.nextPage(IsRepresentativeContactLegalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.ApplicantLegalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "go from Applicant Legal Contact to Is Applicant Legal Contact Uk Based" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(ApplicantLegalContactPage, NormalMode, userAnswers)
                .mustBe(routes.IsApplicantLegalContactUkBasedController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers yes must go to add another legal contact" in {

          forAll(arbitrary[UserAnswers], arbitrary[RepresentativeDetails], arbitrary[UkAddress]) {
            (userAnswers, representativeDetails, ukAddress) =>

              val userAnswersWithAnswer = userAnswers
                .set(IsRepresentativeContactUkBasedPage, true).success.value
                .set(RepresentativeDetailsPage, representativeDetails).success.value
                .set(RepresentativeContactUkAddressPage, ukAddress).success.value
                .set(IsRepresentativeContactLegalContactPage, true).success.value

              navigator.nextPage(IsRepresentativeContactLegalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.AddAnotherLegalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "when going from add another legal contact" when {

        "user answers yes must go to who is secondary legal contact" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(AddAnotherLegalContactPage, true).success.value

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.WhoIsSecondaryLegalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers no must go to select Technical Contact" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(AddAnotherLegalContactPage, false).success.value

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.SelectTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "No answers are found in session must go to session expired controller" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, userAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad)
          }
        }
      }

      "when going from Applicant Legal Contact Uk Based" when {

        "user answers 'yes' must go to Applicant Legal Contact Uk Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsApplicantLegalContactUkBasedPage, true).success.value

              navigator.nextPage(IsApplicantLegalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.ApplicantLegalContactUkAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers 'no' must go to Applicant Legal Contact International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsApplicantLegalContactUkBasedPage, false).success.value

              navigator.nextPage(IsApplicantLegalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.ApplicantLegalContactInternationalAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Applicant Legal Contact Uk Address to Add Another Legal Contact" in {
        forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
          (userAnswers, ukAddress) =>

            val answers = userAnswers.set(ApplicantLegalContactUkAddressPage, ukAddress).success.value

            navigator.nextPage(ApplicantLegalContactUkAddressPage, NormalMode, answers)
              .mustBe(routes.AddAnotherLegalContactController.onPageLoad(NormalMode, answers.id))
        }
      }

      "go from Applicant Legal Contact International Address to Add Another Legal Contact" in {

        forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
          (userAnswers, address) =>

            val answers = userAnswers.set(ApplicantLegalContactInternationalAddressPage, address).success.value

            navigator.nextPage(ApplicantLegalContactInternationalAddressPage, NormalMode, answers)
              .mustBe(routes.AddAnotherLegalContactController.onPageLoad(NormalMode, answers.id))
        }
      }

      "when going from Add Another Legal Contact" when {
        "user answers `yes` must go to Who Is Secondary Legal Contact" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AddAnotherLegalContactPage, true).success.value

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, answers)
                .mustBe(routes.WhoIsSecondaryLegalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers `no` must go to select Technical Contact" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AddAnotherLegalContactPage, false).success.value

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, answers)
                .mustBe(routes.SelectTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Who Is Secondary Legal Contact to Is secondary legal contact Uk based" in {

        forAll(arbitrary[UserAnswers], arbitrary[WhoIsSecondaryLegalContact]) {
          case (userAnswers, whoIsSecondaryLegalContact) =>

            val userAnswersWithTechnicalContact = userAnswers.set(WhoIsSecondaryLegalContactPage, whoIsSecondaryLegalContact).success.value

            navigator.nextPage(WhoIsSecondaryLegalContactPage, NormalMode, userAnswersWithTechnicalContact)
              .mustBe(routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "when going from Applicant Secondary Legal Contact Uk Based" when {

        "user answers 'yes' must go to Applicant Secondary Legal Contact Uk Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

              navigator.nextPage(IsApplicantSecondaryLegalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.ApplicantSecondaryLegalContactUkAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "go from Applicant Secondary Legal Contact Uk Address to select technical contact page" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(ApplicantSecondaryLegalContactUkAddressPage, ukAddress).success.value

              navigator.nextPage(ApplicantSecondaryLegalContactUkAddressPage, NormalMode, answers)
                .mustBe(routes.SelectTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers 'no' must go to Applicant Secondary Legal Contact International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsApplicantSecondaryLegalContactUkBasedPage, false).success.value

              navigator.nextPage(IsApplicantSecondaryLegalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.ApplicantSecondaryLegalContactInternationalAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "go from Applicant Secondary Legal Contact international Address to select technical contact page" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(ApplicantSecondaryLegalContactInternationalAddressPage, internationalAddress).success.value

              navigator.nextPage(ApplicantSecondaryLegalContactInternationalAddressPage, NormalMode, answers)
                .mustBe(routes.SelectTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }


      }


      //       Technical contact section

      "when going from Select Technical Contact" when {
        "user selects the first radio option(rep contact) should go to the add another tech contact page" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(SelectTechnicalContactPage,ContactOptions.RepresentativeContact).success.value

              navigator.nextPage(SelectTechnicalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user selects someone-else must go Who is the technical contacts page" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(SelectTechnicalContactPage, ContactOptions.SomeoneElse).success.value


              navigator.nextPage(SelectTechnicalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.WhoIsTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Who Is Technical Contact to Is Technical Contact Uk Based" in {

        forAll(arbitrary[UserAnswers], arbitrary[TechnicalContact]) {
          case (userAnswers, technicalContact) =>

            val userAnswersWithTechnicalContact = userAnswers.set(WhoIsTechnicalContactPage, technicalContact).success.value

            navigator.nextPage(WhoIsTechnicalContactPage, NormalMode, userAnswersWithTechnicalContact)
              .mustBe(routes.IsTechnicalContactUkBasedController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "when going from Is Technical Contact Uk Based" when {
        "user answers yes must go to Technical Contact International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsTechnicalContactUkBasedPage, true).success.value

              navigator.nextPage(IsTechnicalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.TechnicalContactUkAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers no must go to Technical Contact International Address" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsTechnicalContactUkBasedPage, false).success.value

              navigator.nextPage(IsTechnicalContactUkBasedPage, NormalMode, answers)
                .mustBe(routes.TechnicalContactInternationalAddressController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Technical Contact Uk Address to Add Another Technical Contact" in {

        forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
          (userAnswers, ukAddress) =>

            val answers = userAnswers.set(TechnicalContactUkAddressPage, ukAddress).success.value

            navigator.nextPage(TechnicalContactUkAddressPage, NormalMode, answers)
              .mustBe(routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, answers.id))
        }
      }

      "go from Technical Contact International Address to Add Another Technical Contact" in {

        forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
          (userAnswers, address) =>

            val answers = userAnswers.set(TechnicalContactInternationalAddressPage, address).success.value

            navigator.nextPage(TechnicalContactInternationalAddressPage, NormalMode, answers)
              .mustBe(routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, answers.id))
        }
      }

      "when going from Add Another Technical Contact" must {
        "when user answers is yes" must {
          "go to Select Other Technical Contact on the first pass" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>

                val answers = userAnswers.set(AddAnotherTechnicalContactPage, true).success.value
                  .remove(IpRightsTypePage(0)).success.value

                navigator.nextPage(AddAnotherTechnicalContactPage, NormalMode, answers)
                  .mustBe(routes.SelectOtherTechnicalContactController.onPageLoad(NormalMode, answers.id))
            }
          }
        }
        "when user answers is no" must {
          "go to Ip rights type" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>

                val answers = userAnswers.set(AddAnotherTechnicalContactPage, false).success.value

                navigator.nextPage(AddAnotherTechnicalContactPage, NormalMode, answers)
                  .mustBe(routes.IpRightsTypeController.onPageLoad(NormalMode, 0, answers.id))
            }
          }
        }
        "when the user answers is expired" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              userAnswers.set(AddAnotherTechnicalContactPage, true).success.value

              navigator.nextPage(AddAnotherLegalContactPage, NormalMode, userAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad)
          }
        }
      }

      "when going from Select Other Technical Contact Page" when {
        "user selects the first radio option(rep contact) should go to IP rights controller" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(SelectOtherTechnicalContactPage, ContactOptions.RepresentativeContact).success.value

              navigator.nextPage(SelectOtherTechnicalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.IpRightsTypeController.onPageLoad(NormalMode, 0, userAnswers.id))
          }
        }

        "user selects someone-else must go to Who Is Secondary Technical Contact" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val userAnswersWithAnswer = userAnswers.set(SelectOtherTechnicalContactPage, ContactOptions.SomeoneElse).success.value
                .remove(IpRightsTypePage(0)).success.value

              navigator.nextPage(SelectOtherTechnicalContactPage, NormalMode, userAnswersWithAnswer)
                .mustBe(routes.WhoIsSecondaryTechnicalContactController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from Who Is Secondary Technical Contact" must {
        "go to Is secondary technical contact Uk based (currently goes to Ip Rights Type) on first pass" in {
          forAll(arbitrary[UserAnswers], arbitrary[TechnicalContact]) {
            case (userAnswers, whoIsSecondaryTechnicalContact) =>

              val userAnswersWithTechnicalContact = userAnswers.set(WhoIsSecondaryTechnicalContactPage, whoIsSecondaryTechnicalContact).success.value

              navigator.nextPage(WhoIsSecondaryTechnicalContactPage, NormalMode, userAnswersWithTechnicalContact)
                .mustBe(routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      // Adding goods and rights

      "go from IP Rights Type to the IP Rights Description page for the same IP Right " +
        "when the Type is Copyright, Plant variety, Protected Geographical Indication or Semiconductor topology" in {

        import IpRightsType._

        val typesGen = Gen.oneOf(Copyright, PlantVariety, GeographicalIndication, SemiconductorTopography)

        forAll(arbitrary[UserAnswers], typesGen) {
          (userAnswers, rightsType) =>

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            navigator.nextPage(IpRightsTypePage(0), NormalMode, answers)
              .mustBe(routes.IpRightsDescriptionController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Type to IP Rights Registration Number for the same IP Right " +
        "when the Type is Trademark, Design or Patent" in {

        import IpRightsType._

        val typesGen = Gen.oneOf(Trademark, Design, Patent)

        forAll(arbitrary[UserAnswers], typesGen) {
          (userAnswers, rightsType) =>

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            navigator.nextPage(IpRightsTypePage(0), NormalMode, answers)
              .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Type to IP Rights Supplementary Protection Certificate Type for the same IP Right " +
        "when the Type is Supplementary Protection Certificate" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value

            navigator.nextPage(IpRightsTypePage(0), NormalMode, answers)
              .mustBe(routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Registration Number to IP Rights Registration End for the same IP Right" in {

        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (userAnswers, registrationNumber) =>

            val answers = userAnswers.set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value

            navigator.nextPage(IpRightsRegistrationNumberPage(0), NormalMode, answers)
              .mustBe(routes.IpRightsRegistrationEndController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Supplementary Protection Certificate Type to IP Rights Registration Number for the same IP Right" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(0), NormalMode, userAnswers)
              .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, 0, userAnswers.id))
        }
      }

      "go from IP Rights Registration End to IP Rights Description for the same IP Right " +
        "when the right is a Design or Patent" in {

        forAll(arbitrary[UserAnswers], Gen.oneOf(IpRightsType.Design, IpRightsType.Patent)) {
          (userAnswers, rightsType) =>

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            navigator.nextPage(IpRightsRegistrationEndPage(0), NormalMode, answers)
              .mustBe(routes.IpRightsDescriptionController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Registration End to IP Rights Description With Brand Name for the same IP Right " +
        "when the right is a Trademark" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

            navigator.nextPage(IpRightsRegistrationEndPage(0), NormalMode, answers)
              .mustBe(routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from IP Rights Description to Check IPR Details " +
        "when the right is not a Trademark" in {

        forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
          (userAnswers, rightsType) =>

            whenever(rightsType != IpRightsType.Trademark) {

              val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

              navigator.nextPage(IpRightsDescriptionPage(0), NormalMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
            }
        }
      }

      "go from IP Rights Description With Brand to IP Rights Nice Class" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(IpRightsDescriptionWithBrandPage(0), NormalMode, userAnswers)
              .mustBe(routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, userAnswers.id))
        }
      }

      "go from first IP Rights Nice Class page to the Add Nice Class page" in {

        forAll(arbitrary[UserAnswers], Gen.chooseNum(0, arbitrarilyHighIndex)) {
          (userAnswers, id) =>

            navigator.nextPage(IpRightsNiceClassPage(id, 0), NormalMode, userAnswers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, id, userAnswers.id))
        }
      }

      "go from Add Nice Class page to Add Nice Class page on deleting a Nice Class when Nice Classes still exist in the array" in {

        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>

            val answers = userAnswers.set(IpRightsNiceClassPage(0, 0), niceClass).success.value

            navigator.nextPage(pages.IpRightsRemoveNiceClassPage(0), NormalMode, answers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from Add Nice Class page to Nice Class page on deleting a Nice Class when Nice Classes no longer exist in the array" in {

        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>

            val answers = userAnswers.set(IpRightsNiceClassPage(0, 0), niceClass).success.value
              .remove(RemoveNiceClassQuery(0, 0)).success.value

            navigator.nextPage(pages.IpRightsRemoveNiceClassPage(0), NormalMode, answers)
              .mustBe(routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, answers.id))
        }
      }

      "go from Add Nice Class page to Nice Class page with the next index" in {

        forAll(arbitrary[UserAnswers], Gen.listOf(arbitrary[NiceClassId]).map(_.zipWithIndex)) {
          (userAnswers, niceClasses) =>

            val answersWithNiceClasses: UserAnswers = niceClasses.foldLeft(userAnswers) {
              case (answers, (niceClass, index)) =>
                answers.set(IpRightsNiceClassPage(0, index), niceClass).success.value
            }

            val nextIndex = answersWithNiceClasses.get(NiceClassIdsQuery(0)).map(_.size).getOrElse(0)

            navigator.nextPage(IpRightsAddNiceClassPage(0), NormalMode, answersWithNiceClasses)
              .mustBe(routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, nextIndex, answersWithNiceClasses.id))
        }
      }

      "go from Check IPR Details to Add IP Right" in {

        forAll(arbitrary[UserAnswers], Gen.chooseNum(0, arbitrarilyHighIndex)) {
          (userAnswers, id) =>

            navigator.nextPage(CheckIprDetailsPage(id), NormalMode, userAnswers)
              .mustBe(routes.AddIpRightController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "go from Add IP Right to IP Right Type (the start of the IP Right journey) for a new IP Right" in {

        forAll(arbitrary[UserAnswers], Gen.listOf(arbitrary[IpRightsType]).map(_.zipWithIndex)) {
          (userAnswers, ipRightsTypes) =>

            val answersWithIpRights = ipRightsTypes.foldLeft(userAnswers) {
              case (answers, (ipRightType, index)) =>
                answers.set(IpRightsTypePage(index), ipRightType).success.value
            }

            val nextIpRightsIndex = answersWithIpRights.get(IprDetailsQuery).map(_.size).getOrElse(0)

            navigator.nextPage(AddIpRightPage, NormalMode, answersWithIpRights)
              .mustBe(routes.IpRightsTypeController.onPageLoad(NormalMode, nextIpRightsIndex, answersWithIpRights.id))
        }
      }

      "go from deleting an IPR to the Add IPR page when some IPRs still exist" in {

        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (userAnswers, _) =>

            val answers = userAnswers.set(pages.IpRightsTypePage(0), IpRightsType.Trademark).success.value

            navigator.nextPage(pages.RemoveIprPage, NormalMode, answers)
              .mustBe(routes.AddIpRightController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "go from deleting an IPR to IP Right Type for index 0 when no IPRs remain" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.remove(RemoveIprQuery(0)).success.value

            navigator.nextPage(pages.RemoveIprPage, NormalMode, answers)
              .mustBe(routes.IpRightsTypeController.onPageLoad(NormalMode, 0, answers.id))
        }
      }

      "go from unlock AFA to Check Your Answers when the user answers Yes to unlocking an AFA" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(UnlockAfaPage, true).success.value

            navigator.nextPage(pages.UnlockAfaPage, NormalMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "go from unlock AFA to View Drafts when the user answers No to unlocking an AFA" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(UnlockAfaPage, false).success.value

            navigator.nextPage(pages.UnlockAfaPage, NormalMode, answers)
              .mustBe(routes.ViewDraftsController.onPageLoad())
        }
      }

      "go from Delete Draft to View Drafts" in {

        forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
          (userAnswers, deleteDraft) =>

            val answers = userAnswers.set(DeleteDraftPage, deleteDraft).success.value

            navigator.nextPage(DeleteDraftPage, NormalMode, answers)
              .mustBe(routes.ViewDraftsController.onPageLoad())
        }
      }

      "when going from Delete IPR page" when {
        "user answers yes to delete the IPR must go to the view IPRights page" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(DeleteIpRightPage(0), true).success.value

              navigator.nextPage(pages.DeleteIpRightPage(0), NormalMode, answers)
                .mustBe(routes.AddIpRightController.onDelete(NormalMode, answers.id, 0))
          }
        }
        "user answers no to delete the IPR must go to the view IPRights page" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(DeleteIpRightPage(0), false).success.value

              navigator.nextPage(pages.DeleteIpRightPage(0), NormalMode, answers)
                .mustBe(routes.AddIpRightController.onPageLoad(NormalMode, answers.id))
          }
        }
      }

      "when going from Delete Nice Class page" when {
        "user answers yes to delete the nice class and it is not the only one must go to the nice class added page onDelete" in {
          forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
            (userAnswers, niceClass) =>
              val answers = userAnswers
                .set(DeleteNiceClassPage(0, 0), true).success.value
                .set(IpRightsNiceClassPage(0, 0), niceClass).success.value
                .set(IpRightsNiceClassPage(0, 1), niceClass).success.value

              navigator.nextPage(pages.DeleteNiceClassPage(0, 0), NormalMode, answers)
                .mustBe(routes.IpRightsAddNiceClassController.onDelete(NormalMode, 0, 0, answers.id))
          }
        }
        "user answers yes to delete the nice class and it is the only one must go to the nice class added page onPageLoad" in {
          forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
            (userAnswers, niceClass) =>
              val answers = userAnswers
                .set(DeleteNiceClassPage(0, 0), true).success.value
                .set(IpRightsNiceClassPage(0, 0), niceClass).success.value

              navigator.nextPage(pages.DeleteNiceClassPage(0, 0), NormalMode, answers)
                .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, 0, answers.id))
          }
        }
        "user answers no to delete the nice class must go to the nice class added page onPageLoad" in {
          forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
            (userAnswers, niceClass) =>
              val answers = userAnswers
                .set(DeleteNiceClassPage(0, 0), false).success.value
                .set(IpRightsNiceClassPage(0, 0), niceClass).success.value
                .set(IpRightsNiceClassPage(0, 1), niceClass).success.value

              navigator.nextPage(pages.DeleteNiceClassPage(0, 0), NormalMode, answers)
                .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, 0, answers.id))
          }
        }
      }

      // Additional Information section

      "go from the additional info provided page" when {
        "user answers yes, restricted handling not already answered" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AdditionalInfoProvidedPage, true).success.value
                .remove(RestrictedHandlingPage).success.value

              navigator.nextPage(AdditionalInfoProvidedPage, NormalMode, answers)
                .mustBe(routes.RestrictedHandlingController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
        "user answers yes, restricted handling is already answered" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AdditionalInfoProvidedPage, true).success.value
                .set(RestrictedHandlingPage, false).success.value

              navigator.nextPage(AdditionalInfoProvidedPage, NormalMode, answers)
                .mustBe(routes.RestrictedHandlingController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
        "user answers no must go to permission to destroy small consignments" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AdditionalInfoProvidedPage, false).success.value

              navigator.nextPage(AdditionalInfoProvidedPage, NormalMode, answers)
                .mustBe(routes.PermissionToDestroySmallConsignmentsController.onPageLoad(NormalMode, userAnswers.id))
          }
        }
      }

      "go from the restrictive handling page to Permission to destroy small consignments page" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(RestrictedHandlingPage, NormalMode, userAnswers)
              .mustBe(routes.PermissionToDestroySmallConsignmentsController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "go from Permission to Destroy Small Consignments to Is Ex-officio" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(PermissionToDestroySmallConsignmentsPage, NormalMode, userAnswers)
              .mustBe(routes.IsExOfficioController.onPageLoad(NormalMode, userAnswers.id))
        }
      }

      "when going from Is Ex-officio" when {
        "user answers Yes must go to Wants One Year Rights Protection" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsExOfficioPage, true).success.value

              navigator.nextPage(IsExOfficioPage, NormalMode, answers)
                .mustBe(routes.WantsOneYearRightsProtectionController.onPageLoad(NormalMode, userAnswers.id))
          }
        }

        "user answers No must go to Company Applying" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsExOfficioPage, false).success.value

              navigator.nextPage(IsExOfficioPage, NormalMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(userAnswers.id))
          }
        }
      }

      "go from Wants One Year Rights Protection to Company Applying" in {

        forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
          (userAnswers, answer) =>

            val answers = userAnswers.set(WantsOneYearRightsProtectionPage, answer).success.value

            navigator.nextPage(WantsOneYearRightsProtectionPage, NormalMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(userAnswers.id))
        }
      }

      "must go to Application Receipt Date in Normal Mode as the first page" in {

        forAll(arbitrary[AfaId]) {
          afaId =>
            navigator.firstPage(afaId) mustBe routes.ApplicationReceiptDateController.onPageLoad(NormalMode, afaId)
        }
      }
    }
  }
}
