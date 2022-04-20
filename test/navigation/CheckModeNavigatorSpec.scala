/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package navigation

import controllers.routes
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import queries.{IprDetailsQuery, NiceClassIdsQuery, RemoveIprQuery, RemoveNiceClassQuery}

import java.time.LocalDate

class CheckModeNavigatorSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite
  with TryValues with ScalaCheckPropertyChecks with Generators with OptionValues {

  val navigator = new Navigator

  val afaId: AfaId = arbitrary[AfaId].sample.value

  val arbitrarilyHighIndex = 100

  "Navigator in check mode" - {

    "must go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

      case object UnknownPage extends Page

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(UnknownPage, CheckMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(userAnswers.id)
      }
    }
    "must go from Additional Info" - {

      "to restricted handling when the user answers Yes and restricted handling has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(AdditionalInfoProvidedPage, true).success.value
                .remove(RestrictedHandlingPage).success.value

            navigator.nextPage(AdditionalInfoProvidedPage, CheckMode, answers)
              .mustBe(routes.RestrictedHandlingController.onPageLoad(CheckMode, answers.id))
        }
      }

      "to Check Your Answers" - {

        "when the user answers No" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AdditionalInfoProvidedPage, false).success.value

              navigator.nextPage(AdditionalInfoProvidedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers Yes and Restricted Handling has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
            (userAnswers, restrictedHandling) =>

              val answers =
                userAnswers
                  .set(AdditionalInfoProvidedPage, true).success.value
                  .set(RestrictedHandlingPage, restrictedHandling).success.value

              navigator.nextPage(AdditionalInfoProvidedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }
    }


    "must go from Is Ex Officio" - {

      "to Wants 1 Year Rights Protection when the user answers Yes and Wants 1 Year Rights Protection has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsExOfficioPage, true).success.value
                .remove(WantsOneYearRightsProtectionPage).success.value

            navigator.nextPage(IsExOfficioPage, CheckMode, answers)
              .mustBe(routes.WantsOneYearRightsProtectionController.onPageLoad(CheckMode, answers.id))
        }
      }

      "to Check Your Answers" - {

        "when the user answers No" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsExOfficioPage, false).success.value

              navigator.nextPage(IsExOfficioPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers Yes and Wants 1 Year Rights Protection has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
            (userAnswers, wantsProtection) =>

              val answers =
                userAnswers
                  .set(IsExOfficioPage, true).success.value
                  .set(WantsOneYearRightsProtectionPage, wantsProtection).success.value

              navigator.nextPage(IsExOfficioPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }
    }

    "must go from Is Representative Contact UK Based" - {

      "to Check Your Answers" - {

        "when the user answers Yes and Representative Contact UK Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
            (userAnswers, ukAddress) =>

              val answers =
                userAnswers
                  .set(IsRepresentativeContactUkBasedPage, true).success.value
                  .set(RepresentativeContactUkAddressPage, ukAddress).success.value

              navigator.nextPage(IsRepresentativeContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers No and Representative Contact International Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
            (userAnswers, address) =>

              val answers =
                userAnswers
                  .set(IsRepresentativeContactUkBasedPage, false).success.value
                  .set(RepresentativeContactInternationalAddressPage, address).success.value

              navigator.nextPage(IsRepresentativeContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "to Representative Contact Uk Address when the user answers Yes and Representative Contact UK Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsRepresentativeContactUkBasedPage, true).success.value
                .remove(RepresentativeContactUkAddressPage).success.value

            navigator.nextPage(IsRepresentativeContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.RepresentativeContactUkAddressController.onPageLoad(CheckMode, answers.id))
        }
      }

      "to Representative Contact International Address when the answer is No and Representative Contact International Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsRepresentativeContactUkBasedPage, false).success.value
                .remove(RepresentativeContactInternationalAddressPage).success.value

            navigator.nextPage(IsRepresentativeContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.RepresentativeContactInternationalAddressController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from Is Representative Contact Legal Contact" - {

      "to Check Your Answers" - {

        "when the answer is Yes" in {

          forAll(arbitrary[UserAnswers], arbitrary[RepresentativeDetails], arbitrary[UkAddress]) {
            (userAnswers, representativeDetails, ukAddress) =>

              val answers = userAnswers
                .set(IsRepresentativeContactUkBasedPage, true).success.value
                .set(RepresentativeDetailsPage, representativeDetails).success.value
                .set(RepresentativeContactUkAddressPage, ukAddress).success.value
                .set(IsRepresentativeContactLegalContactPage, true).success.value

              navigator.nextPage(IsRepresentativeContactLegalContactPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the answer is No and Who Is Legal Contact has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[ApplicantLegalContact]) {
            (userAnswers, applicantLegalContact) =>

              val answers =
                userAnswers
                  .set(IsRepresentativeContactLegalContactPage, false).success.value
                  .set(ApplicantLegalContactPage, applicantLegalContact).success.value

              navigator.nextPage(IsRepresentativeContactLegalContactPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }
    }

    "must go from Is Applicant Legal UK Contact Based" - {

      "to Check Your Answers" - {

        "when the user answers Yes and Applicant Legal Contact UK Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
            (userAnswers, ukAddress) =>

              val answers =
                userAnswers
                  .set(IsApplicantLegalContactUkBasedPage, true).success.value
                  .set(ApplicantLegalContactUkAddressPage, ukAddress).success.value

              navigator.nextPage(IsApplicantLegalContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers No and Applicant Legal Contact International Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
            (userAnswers, address) =>

              val answers =
                userAnswers
                  .set(IsApplicantLegalContactUkBasedPage, false).success.value
                  .set(ApplicantLegalContactInternationalAddressPage, address).success.value

              navigator.nextPage(IsApplicantLegalContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "to Applicant Legal Contact Uk Address when the user answers Yes and Applicant Legal Contact UK Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsApplicantLegalContactUkBasedPage, true).success.value
                .remove(ApplicantLegalContactUkAddressPage).success.value

            navigator.nextPage(IsApplicantLegalContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.ApplicantLegalContactUkAddressController.onPageLoad(CheckMode, answers.id))
        }
      }

      "to Applicant Legal Contact International Address when the answer is No and Applicant Legal Contact International Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsApplicantLegalContactUkBasedPage, false).success.value
                .remove(ApplicantLegalContactInternationalAddressPage).success.value

            navigator.nextPage(IsApplicantLegalContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.ApplicantLegalContactInternationalAddressController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from Who Is Secondary Legal Contact" - {

      "to Check Your Answers when Is Applicant Secondary Legal Contact UK based has been answered" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value

            navigator.nextPage(WhoIsSecondaryLegalContactPage, CheckMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "to Is Applicant Secondary Legal Contact UK based when Is Applicant Secondary Legal Contact UK based has not been answered" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.remove(IsApplicantSecondaryLegalContactUkBasedPage).success.value

            navigator.nextPage(WhoIsSecondaryLegalContactPage, CheckMode, answers)
              .mustBe(routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from Select Other Technical Contact" - {

      "to Check Your Answers" - {

        "when Select Other Technical Contact is NOT Someone Else" in {

          val contactOptionsTypeGen = Gen.oneOf(ContactOptions.RepresentativeContact, ContactOptions.LegalContact, ContactOptions.SecondaryLegalContact)

          forAll(arbitrary[UserAnswers], contactOptionsTypeGen) {
            (userAnswers, contactOptionsType) =>

              val answers = userAnswers.set(SelectOtherTechnicalContactPage, contactOptionsType).success.value

              navigator.nextPage(SelectOtherTechnicalContactPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

      }

      "to Who Is Secondary Technical Contact" - {

        "When Select Other Technical Contact is Someone Else" - {

          forAll(arbitrary[UserAnswers]) {
            (userAnswers) =>

              val answers = userAnswers.set(SelectOtherTechnicalContactPage, ContactOptions.SomeoneElse).success.value

              navigator.nextPage(SelectOtherTechnicalContactPage, CheckMode, answers)
                .mustBe(routes.WhoIsSecondaryTechnicalContactController.onPageLoad(CheckMode, answers.id))

          }

        }
      }
    }

    "must go from Who Is Technical Contact" - {

      "to Check Your Answers" - {

        "when Is Technical Contact UK Based has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
            (userAnswers, ukBased) =>

              val answers = userAnswers.set(IsTechnicalContactUkBasedPage, ukBased).success.value

              navigator.nextPage(WhoIsTechnicalContactPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers Yes and Technical Contact UK Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
            (userAnswers, ukAddress) =>

              val answers =
                userAnswers
                  .set(IsTechnicalContactUkBasedPage, true).success.value
                  .set(TechnicalContactUkAddressPage, ukAddress).success.value

              navigator.nextPage(IsTechnicalContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "to Is Technical Contact UK Based when Is Technical Contact UK Based has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.remove(IsTechnicalContactUkBasedPage).success.value

            navigator.nextPage(WhoIsTechnicalContactPage, CheckMode, answers)
              .mustBe(routes.IsTechnicalContactUkBasedController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from Is Technical Contact Uk Based" - {

      "to Check Your Answers" - {

        "when the user answers Yes and Technical Contact UK Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[UkAddress]) {
            (userAnswers, address) =>

              val answers =
                userAnswers
                  .set(IsTechnicalContactUkBasedPage, true).success.value
                  .set(TechnicalContactUkAddressPage, address).success.value

              navigator.nextPage(IsTechnicalContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "when the user answers No and Technical Contact International Address has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[InternationalAddress]) {
            (userAnswers, address) =>

              val answers =
                userAnswers
                  .set(IsTechnicalContactUkBasedPage, false).success.value
                  .set(TechnicalContactInternationalAddressPage, address).success.value

              navigator.nextPage(IsTechnicalContactUkBasedPage, CheckMode, answers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
          }
        }
      }

      "to Technical Contact UK Address when the user answers Yes and Technical Contact UK Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsTechnicalContactUkBasedPage, true).success.value
                .remove(TechnicalContactUkAddressPage).success.value

            navigator.nextPage(IsTechnicalContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.TechnicalContactUkAddressController.onPageLoad(CheckMode, answers.id))
        }
      }

      "to Technical Contact International Address when the user answers No and Technical Contact International Address has not been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers =
              userAnswers
                .set(IsTechnicalContactUkBasedPage, false).success.value
                .remove(TechnicalContactInternationalAddressPage).success.value

            navigator.nextPage(IsTechnicalContactUkBasedPage, CheckMode, answers)
              .mustBe(routes.TechnicalContactInternationalAddressController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from Who Is Secondary Technical Contact" - {

      "to Check Your Answers when Is Secondary Technical Contact UK based has been answered" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(IsSecondaryTechnicalContactUkBasedPage, true).success.value

            navigator.nextPage(WhoIsSecondaryTechnicalContactPage, CheckMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "to Is Secondary Technical Contact UK based when Is Secondary Technical Contact UK based has not been answered" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.remove(IsSecondaryTechnicalContactUkBasedPage).success.value

            navigator.nextPage(WhoIsSecondaryTechnicalContactPage, CheckMode, answers)
              .mustBe(routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(CheckMode, answers.id))
        }
      }
    }

    "must go from IP Rights Type" - {

      "to Check IPR Details for the same IP Right" - {

        "when the type is Copyright, Plant variety, Geographical Indication or Semiconductor topology and " +
          "IP Rights Description has been answered" in {

          import models.IpRightsType._

          val rightsTypeGen = Gen.oneOf(Copyright, PlantVariety, GeographicalIndication, SemiconductorTopography)

          forAll(arbitrary[UserAnswers], rightsTypeGen, arbitrary[String]) {
            (userAnswers, rightType, description) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), rightType).success.value
                  .set(IpRightsDescriptionPage(0), description).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }

        "when the type is Design or Patent and IP Right Registration Number and Description has been answered" in {

          import models.IpRightsType._

          forAll(arbitrary[UserAnswers], Gen.oneOf(Design, Patent), arbitrary[String], arbitrary[String]) {
            (userAnswers, rightType, registrationNumber, description) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), rightType).success.value
                  .set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value
                  .set(IpRightsDescriptionPage(0), description).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }

        "when the type is Trademark and Registration Number, Description With Brand and NICE classes have been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[String], arbitrary[String], arbitrary[NiceClassId]) {
            (userAnswers, registrationNumber, brand, description, niceClass) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value
                  .set(IpRightsDescriptionWithBrandPage(0), IpRightsDescriptionWithBrand(brand, description)).success.value
                  .set(IpRightsNiceClassPage(0, 0), niceClass).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }

        "when the type is Supplementary Protection Certificate and Supplementary Protection Certificate Type has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsSupplementaryProtectionCertificateType]) {
            (userAnswers, certificateType) =>

              val answers =
                userAnswers
                  .set(IpRightsSupplementaryProtectionCertificateTypePage(0), certificateType).success.value
                  .set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Description" - {

        "when the type is Copyright, Plant variety or Semiconductor topology and " +
          "IP Rights Description has not been answered" in {

          import models.IpRightsType._

          val rightsTypeGen = Gen.oneOf(Copyright, PlantVariety, SemiconductorTopography)

          forAll(arbitrary[UserAnswers], rightsTypeGen) {
            (userAnswers, rightType) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), rightType).success.value
                  .remove(IpRightsDescriptionPage(0)).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsDescriptionController.onPageLoad(CheckMode, 0, answers.id))
          }
        }

        "when the type is Design or Patent, Registration Number has been answered and Description has not been answered" in {

          val rightsTypeGen = Gen.oneOf(IpRightsType.Design, IpRightsType.Patent)

          forAll(arbitrary[UserAnswers], rightsTypeGen, arbitrary[String]) {
            (userAnswers, rightsType, registrationNumber) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), rightsType).success.value
                  .set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value
                  .remove(IpRightsDescriptionPage(0)).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsDescriptionController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP RIghts Description With Brand" - {

        "when the type is Trademark and Registration Number, Description and NICE classes have been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[String], arbitrary[NiceClassId]) {
            (userAnswers, registrationNumber, description, niceClass) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value
                  .set(IpRightsDescriptionPage(0), description).success.value
                  .remove(IpRightsDescriptionWithBrandPage(0)).success.value
                  .set(IpRightsNiceClassPage(0, 0), niceClass).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsDescriptionWithBrandController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Registration Number" - {

        "when the type is Trademark, Design or Patent and IP Right Registration Number has not been answered" in {

          import models.IpRightsType._

          forAll(arbitrary[UserAnswers], Gen.oneOf(Trademark, Design, Patent)) {
            (userAnswers, rightType) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), rightType).success.value
                  .remove(IpRightsRegistrationNumberPage(0)).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to Supplementary Protection Certificate Type" - {

        "when the type is Supplementary Protection Certificate and Supplementary Protection Certificate Type has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value
                  .remove(IpRightsSupplementaryProtectionCertificateTypePage(0)).success.value

              navigator.nextPage(IpRightsTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Supplementary Protection Certificate Type" - {

      "to Check IPR Details for the same IP right" - {

        "when IP Rights Registration Number has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (userAnswers, registrationNumber) =>

              val answers =
                userAnswers
                  .set(IpRightsRegistrationNumberPage(0), registrationNumber).success.value

              navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Registration Number for the same IP right" - {

        "when IP Rights Registration Number has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.remove(IpRightsRegistrationNumberPage(0)).success.value

              navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(0), CheckMode, answers)
                .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Registration Number" - {

      "to Check IPR Details" - {

        "when IP Right Registration End has been answered" in {

          val dateGen = datesBetween(LocalDate.now, LocalDate.now.plusYears(100))

          forAll(arbitrary[UserAnswers], dateGen) {
            (userAnswers, date) =>

              val answers = userAnswers.set(IpRightsRegistrationEndPage(0), date).success.value

              navigator.nextPage(IpRightsRegistrationNumberPage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP Right Registration End" - {

        "when IP Right Registration End has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.remove(IpRightsRegistrationEndPage(0)).success.value

              navigator.nextPage(IpRightsRegistrationNumberPage(0), CheckMode, answers)
                .mustBe(routes.IpRightsRegistrationEndController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Registration End" - {

      "to Check IPR Details" - {

        "when the right is a Trademark and Description With Brand has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[String]) {
            (userAnswers, brand, description) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .set(IpRightsDescriptionWithBrandPage(0), IpRightsDescriptionWithBrand(brand, description)).success.value

              navigator.nextPage(IpRightsRegistrationEndPage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, userAnswers.id))
          }
        }

        "when the right is not a Trademark and IP Rights Description has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsType], arbitrary[String]) {
            (userAnswers, rightType, description) =>

              whenever(rightType != IpRightsType.Trademark) {

                val answers =
                  userAnswers
                    .set(IpRightsTypePage(0), rightType).success.value
                    .set(IpRightsDescriptionPage(0), description).success.value

                navigator.nextPage(IpRightsRegistrationEndPage(0), CheckMode, answers)
                  .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
              }
          }
        }
      }

      "to IP Rights Shares Brand Name" - {

        "when the right is a Trademark and Description With Brand has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .remove(IpRightsDescriptionWithBrandPage(0)).success.value

              navigator.nextPage(IpRightsRegistrationEndPage(0), CheckMode, answers)
                .mustBe(routes.IpRightsDescriptionWithBrandController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Description" - {

        "when the right is not a Trademark and IP Rights Description has not been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
            (userAnswers, rightType) =>

              whenever(rightType != IpRightsType.Trademark) {

                val answers =
                  userAnswers
                    .set(IpRightsTypePage(0), rightType).success.value
                    .remove(IpRightsDescriptionPage(0)).success.value

                navigator.nextPage(IpRightsRegistrationEndPage(0), CheckMode, answers)
                  .mustBe(routes.IpRightsDescriptionController.onPageLoad(CheckMode, 0, answers.id))
              }
          }
        }
      }
    }

    "must go from IP Rights Description" - {

      "to Check IPR Details for the same IP Right" - {

        "when the right is not a trademark" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
            (userAnswers, rightsType) =>

              whenever(rightsType != IpRightsType.Trademark) {

                val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

                navigator.nextPage(IpRightsDescriptionPage(0), CheckMode, answers)
                  .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
              }
          }
        }

        "when the right is a trademark and NICE classes have been added" in {

          forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
            (userAnswers, niceClass) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .set(IpRightsNiceClassPage(0, 0), niceClass).success.value

              navigator.nextPage(IpRightsDescriptionPage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to Nice Class 0 for the same IP Right" - {

        "when the right is a Trademark and NICE classes have not been added" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .remove(IpRightsNiceClassPage(0, 0)).success.value

              navigator.nextPage(IpRightsDescriptionPage(0), CheckMode, answers)
                .mustBe(routes.IpRightsNiceClassController.onPageLoad(CheckMode, 0, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Description With Brand" - {

      "to Check IP Right Details for the same IP Right" - {

        "when NICE classes have been added" in {

          forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
            (userAnswers, niceClass) =>

              val answers = userAnswers.set(IpRightsNiceClassPage(0, 0), niceClass).success.value

              navigator.nextPage(IpRightsDescriptionWithBrandPage(0), CheckMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(CheckMode, 0, answers.id))
          }
        }
      }

      "to NICE Class 0 for the same IP Right" - {

        "when the right is a Trademark and NICE classes have not been added" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .remove(IpRightsNiceClassPage(0, 0)).success.value

              navigator.nextPage(IpRightsDescriptionWithBrandPage(0), CheckMode, answers)
                .mustBe(routes.IpRightsNiceClassController.onPageLoad(CheckMode, 0, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Nice Class" - {

      "to Add NICE classes in Check Mode" in {

        forAll(arbitrary[UserAnswers], Gen.chooseNum(0, arbitrarilyHighIndex), Gen.chooseNum(0, arbitrarilyHighIndex)) {
          (userAnswers, iprIndex, niceIndex) =>

            navigator.nextPage(IpRightsNiceClassPage(iprIndex, niceIndex), CheckMode, userAnswers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(CheckMode, iprIndex, userAnswers.id))
        }
      }
    }

    "must go from Delete Nice Class page" - {
      "user answers yes to delete the nice class and it is not the only one must go to the nice class added page onDelete" in {
        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>
            val answers = userAnswers
              .set(DeleteNiceClassPage(0, 0), true).success.value
              .set(IpRightsNiceClassPage(0, 0), niceClass).success.value
              .set(IpRightsNiceClassPage(0, 1), niceClass).success.value

            navigator.nextPage(pages.DeleteNiceClassPage(0, 0), CheckMode, answers)
              .mustBe(routes.IpRightsAddNiceClassController.onDelete(CheckMode, 0, 0, answers.id))
        }
      }
      "user answers yes to delete the nice class and it is the only one must go to the nice class added page onPageLoad" in {
        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>
            val answers = userAnswers
              .set(DeleteNiceClassPage(0, 0), true).success.value
              .set(IpRightsNiceClassPage(0, 0), niceClass).success.value

            navigator.nextPage(pages.DeleteNiceClassPage(0, 0), CheckMode, answers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(CheckMode, 0, answers.id))
        }
      }
      "user answers no to delete the nice class must go to the nice class added page onPageLoad" in {
        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>
            val answers = userAnswers
              .set(DeleteNiceClassPage(0, 0), false).success.value
              .set(IpRightsNiceClassPage(0, 0), niceClass).success.value
              .set(IpRightsNiceClassPage(0, 1), niceClass).success.value

            navigator.nextPage(pages.DeleteNiceClassPage(0, 0), CheckMode, answers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(CheckMode, 0, answers.id))
        }
      }
    }

    "must go from Remove Nice Class page" - {
      "when Nice Classes still exist in the array, to IP right add nice class page onPageLoad" in {
        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>

            val answers = userAnswers.set(IpRightsNiceClassPage(0, 0), niceClass).success.value

            navigator.nextPage(pages.IpRightsRemoveNiceClassPage(0), CheckMode, answers)
              .mustBe(routes.IpRightsAddNiceClassController.onPageLoad(CheckMode, 0, answers.id))
        }
      }
      "when Nice Class no longer exists in the array, to IP right add Nice Class page onPageLoad with default niceCLassIndex of 0" in {
        forAll(arbitrary[UserAnswers], arbitrary[NiceClassId]) {
          (userAnswers, niceClass) =>

            val answers = userAnswers.set(IpRightsNiceClassPage(0, 0), niceClass).success.value
              .remove(RemoveNiceClassQuery(0, 0)).success.value

            navigator.nextPage(pages.IpRightsRemoveNiceClassPage(0), CheckMode, answers)
              .mustBe(routes.IpRightsNiceClassController.onPageLoad(CheckMode, 0, 0, answers.id))
        }
      }
    }
    "must go from Add Nice Class page to Nice Class page with the next index" in {

      forAll(arbitrary[UserAnswers], Gen.listOf(arbitrary[NiceClassId]).map(_.zipWithIndex)) {
        (userAnswers, niceClasses) =>

          val answersWithNiceClasses: UserAnswers = niceClasses.foldLeft(userAnswers) {
            case (answers, (niceClass, index)) =>
              answers.set(IpRightsNiceClassPage(0, index), niceClass).success.value
          }

          val nextIndex = answersWithNiceClasses.get(NiceClassIdsQuery(0)).map(_.size).getOrElse(0)

          navigator.nextPage(IpRightsAddNiceClassPage(0), CheckMode, answersWithNiceClasses)
            .mustBe(routes.IpRightsNiceClassController.onPageLoad(CheckMode, 0, nextIndex, answersWithNiceClasses.id))
      }
    }

    "must go from Add IP Right to IP Right Type (the start of the IP Right journey) for a new IP Right" in {

      forAll(arbitrary[UserAnswers], Gen.listOf(arbitrary[IpRightsType]).map(_.zipWithIndex)) {
        (userAnswers, ipRightsTypes) =>

          val answersWithIpRights = ipRightsTypes.foldLeft(userAnswers) {
            case (answers, (ipRightType, index)) =>
              answers.set(IpRightsTypePage(index), ipRightType).success.value
          }

          val nextIpRightsIndex = answersWithIpRights.get(IprDetailsQuery).map(_.size).getOrElse(0)

          navigator.nextPage(AddIpRightPage, CheckMode, answersWithIpRights)
            .mustBe(routes.IpRightsTypeController.onPageLoad(CheckMode, nextIpRightsIndex, answersWithIpRights.id))
      }
    }

    "must go from deleting an IPR to the Add IPR page when some IPRs still exist" in {

      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (userAnswers, _) =>

          val answers = userAnswers.set(pages.IpRightsTypePage(0), IpRightsType.Trademark).success.value

          navigator.nextPage(pages.RemoveIprPage, CheckMode, answers)
            .mustBe(routes.AddIpRightController.onPageLoad(CheckMode, userAnswers.id))
      }
    }

    "must go from deleting an IPR to IP Right Type for index 0 when no IPRs remain" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(RemoveIprQuery(0)).success.value

          navigator.nextPage(pages.RemoveIprPage, CheckMode, answers)
            .mustBe(routes.IpRightsTypeController.onPageLoad(CheckMode, 0, answers.id))
      }
    }

  }
}
