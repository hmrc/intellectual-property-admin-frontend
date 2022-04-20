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
import queries.{IprDetailsQuery, RemoveIprQuery}

import java.time.LocalDate

class ModifyModeNavigatorSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite
  with TryValues with ScalaCheckPropertyChecks with Generators with OptionValues {

  val navigator = new Navigator

  val afaId: AfaId = arbitrary[AfaId].sample.value

  val arbitrarilyHighIndex = 100

  "Navigator in check mode" - {

    "must go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

      case object UnknownPage extends Page

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(UnknownPage, ModifyMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(userAnswers.id)
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
          }
        }

        "when the type is Supplementary Protection Certificate and Supplementary Protection Certificate Type has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsSupplementaryProtectionCertificateType]) {
            (userAnswers, certificateType) =>

              val answers =
                userAnswers
                  .set(IpRightsSupplementaryProtectionCertificateTypePage(0), certificateType).success.value
                  .set(IpRightsTypePage(0), IpRightsType.SupplementaryProtectionCertificate).success.value

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsDescriptionController.onPageLoad(ModifyMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsDescriptionController.onPageLoad(ModifyMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Description With Brand" - {

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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsDescriptionWithBrandController.onPageLoad(ModifyMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(ModifyMode, 0, answers.id))
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

              navigator.nextPage(IpRightsTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(ModifyMode, 0, answers.id))
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

              navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
          }
        }
      }

      "to IP Rights Registration Number for the same IP right" - {

        "when IP Rights Registration Number has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.remove(IpRightsRegistrationNumberPage(0)).success.value

              navigator.nextPage(IpRightsSupplementaryProtectionCertificateTypePage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsRegistrationNumberController.onPageLoad(ModifyMode, 0, answers.id))
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

              navigator.nextPage(IpRightsRegistrationNumberPage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
          }
        }
      }

      "to IP Right Registration End" - {

        "when IP Right Registration End has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.remove(IpRightsRegistrationEndPage(0)).success.value

              navigator.nextPage(IpRightsRegistrationNumberPage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsRegistrationEndController.onPageLoad(ModifyMode, 0, answers.id))
          }
        }
      }
    }

    "must go from IP Rights Registration End" - {

      "to Check IPR Details" - {

        "when the right is a Trademark and Description With Brand has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[IpRightsDescriptionWithBrand]) {
            (userAnswers, descriptionWithBrand) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .set(IpRightsDescriptionWithBrandPage(0),descriptionWithBrand).success.value

              navigator.nextPage(IpRightsRegistrationEndPage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, userAnswers.id))
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

                navigator.nextPage(IpRightsRegistrationEndPage(0), ModifyMode, answers)
                  .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
              }
          }
        }
      }

      "to IP Rights Shares Brand Name" - {

        "when the right is a Trademark and Brand With Description has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            (userAnswers) =>

              val answers =
                userAnswers
                  .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
                  .remove(IpRightsDescriptionWithBrandPage(0)).success.value

              navigator.nextPage(IpRightsRegistrationEndPage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsDescriptionWithBrandController.onPageLoad(ModifyMode, 0, answers.id))
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

                navigator.nextPage(IpRightsRegistrationEndPage(0), ModifyMode, answers)
                  .mustBe(routes.IpRightsDescriptionController.onPageLoad(ModifyMode, 0, answers.id))
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

                navigator.nextPage(IpRightsDescriptionPage(0), ModifyMode, answers)
                  .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsDescriptionPage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsDescriptionPage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, answers.id))
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

              navigator.nextPage(IpRightsDescriptionWithBrandPage(0), ModifyMode, answers)
                .mustBe(routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, answers.id))
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

              navigator.nextPage(IpRightsDescriptionWithBrandPage(0), ModifyMode, answers)
                .mustBe(routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, answers.id))
          }
        }
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

          navigator.nextPage(AddIpRightPage, ModifyMode, answersWithIpRights)
            .mustBe(routes.IpRightsTypeController.onPageLoad(ModifyMode, nextIpRightsIndex, answersWithIpRights.id))
      }
    }

    "must go from deleting an IPR " - {
      "to add IP Right Type for index 0" - {
        "when no IPRs remain" in {

          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.remove(RemoveIprQuery(0)).success.value

              navigator.nextPage(pages.RemoveIprPage, ModifyMode, answers)
                .mustBe(routes.IpRightsTypeController.onPageLoad(ModifyMode, 0, answers.id))
          }
        }
      }
    }
  }
}
