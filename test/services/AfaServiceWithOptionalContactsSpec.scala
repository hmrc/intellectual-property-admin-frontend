/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import connectors.AfaConnector
import generators.AfaGenerators
import models.CompanyApplyingIsRightsHolder._
import models.afa._
import models.{AfaId, ApplicantLegalContact, CompanyApplying, CompanyApplyingIsRightsHolder, ContactOptions, InternationalAddress, IpRightsDescriptionWithBrand, IpRightsSupplementaryProtectionCertificateType, IpRightsType, NiceClassId, RepresentativeDetails, TechnicalContact, UkAddress, UserAnswers, WhoIsSecondaryLegalContact, afa}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalacheck.Arbitrary._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@nowarn("msg=not.*?exhaustive")
class AfaServiceWithOptionalContactsSpec
    extends AnyFreeSpec
    with Matchers
    with TryValues
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with OptionValues
    with AfaGenerators {

  private val afaConnector    = mock[AfaConnector]
  private val contactsService = mock[ContactsService]

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    Mockito.reset(afaConnector)
    Mockito.reset(contactsService)
    super.beforeEach()
  }

  "Afa service" - {

    "for a draft with no IP rights" - {

      "must not be able to create an afa" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val endDate = LocalDate.now

        val applicant: Company = arbitrary[Company].sample.value.copy(
          applicantAddressUkBased = true,
          applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
          applicantType = Authorised
        )

        val expectedRequest = afa.InitialAfa(
          id = arbitraryGbAfaIdThreeDigit().sample.value,
          receiptDate = None,
          additionalInfoProvided = true,
          shareWithEuropeanCommission = Some(true),
          permissionToDestroySmallConsignments = true,
          exOfficio = Some(ExOfficio(true)),
          applicant = applicant,
          legalContact = genContactUk.sample.value,
          technicalContact = genContactUk.sample.value,
          endDate = endDate,
          ipRights = Seq.empty,
          representativeContact = genRepresentativeContact.sample.value,
          isRestrictedHandling = Some(false),
          secondaryLegalContact = Some(genSecondaryLegalContactUk.sample.value),
          secondaryTechnicalContact = Some(genSecondaryTechnicalContactUk.sample.value)
        )

        val Contact(
          legalCompanyName,
          legalContactName,
          legalContactPhone,
          legalContactOtherPhone,
          legalContactEmail,
          legalContactAddress: UkAddress
        ) = expectedRequest.legalContact
        val Contact(
          techContactCompanyName,
          techContactName,
          techContactPhone,
          _,
          techContactEmail,
          techContactAddress: UkAddress
        ) = expectedRequest.technicalContact
        val Contact(
          secondaryTechnicalContactCompanyName,
          secondaryTechnicalContactName,
          secondaryTechnicalContactPhone,
          _,
          secondaryTechnicalContactEmail,
          secondaryTechnicalContactAddress: UkAddress
        ) = expectedRequest.secondaryTechnicalContact.get
        val Contact(
          secondaryLegalCompanyName,
          secondaryLegalContactName,
          secondaryLegalContactPhone,
          _,
          secondaryLegalContactEmail,
          secondaryLegalContactAddress: UkAddress
        ) = expectedRequest.secondaryLegalContact.get
        val RepresentativeContact(
          contactName,
          companyName,
          roleOrPosition,
          phone,
          email,
          address: UkAddress,
          Some(evidenceOfPowerToAct)
        ) = expectedRequest.representativeContact

        val userAnswers = {

          val base = UserAnswers(expectedRequest.id)
            .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
            .success
            .value
            .set(IsExOfficioPage, true)
            .success
            .value
            .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
            .success
            .value
            .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
            .success
            .value
            .set(IsCompanyApplyingUkBasedPage, true)
            .success
            .value
            .set(CompanyApplyingUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
            .success
            .value
            .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.Authorised)
            .success
            .value
            .set(IsRepresentativeContactLegalContactPage, false)
            .success
            .value
            .set(
              ApplicantLegalContactPage,
              ApplicantLegalContact(
                legalContactName,
                legalCompanyName,
                legalContactPhone,
                legalContactOtherPhone,
                legalContactEmail
              )
            )
            .success
            .value
            .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
            .success
            .value
            .set(IsApplicantLegalContactUkBasedPage, true)
            .success
            .value
            .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
            .success
            .value
            .set(
              WhoIsTechnicalContactPage,
              TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
            )
            .success
            .value
            .set(IsTechnicalContactUkBasedPage, true)
            .success
            .value
            .set(TechnicalContactUkAddressPage, techContactAddress)
            .success
            .value
            .set(
              RepresentativeDetailsPage,
              RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email)
            )
            .success
            .value
            .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
            .success
            .value
            .set(IsRepresentativeContactUkBasedPage, true)
            .success
            .value
            .set(RepresentativeContactUkAddressPage, address)
            .success
            .value
            .set(IsSecondaryTechnicalContactUkBasedPage, true)
            .success
            .value
            .set(
              WhoIsSecondaryTechnicalContactPage,
              TechnicalContact(
                secondaryTechnicalContactCompanyName,
                secondaryTechnicalContactName,
                secondaryTechnicalContactPhone,
                secondaryTechnicalContactEmail
              )
            )
            .success
            .value
            .set(TechnicalContactUkAddressPage, secondaryTechnicalContactAddress)
            .success
            .value
            .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
            .success
            .value
            .set(
              WhoIsSecondaryLegalContactPage,
              WhoIsSecondaryLegalContact(
                secondaryLegalContactName,
                secondaryLegalCompanyName,
                secondaryLegalContactPhone,
                secondaryLegalContactEmail
              )
            )
            .success
            .value
            .set(ApplicantSecondaryLegalContactUkAddressPage, secondaryLegalContactAddress)
            .success
            .value

          val json = Json.obj(
            "ipRights" -> Json.arr()
          )

          base.copy(data = json ++ base.data)
        }
        service.canCreateAfa(userAnswers) mustBe false
      }
    }

    "for company that is authorised and evidence is not provided" - {

      val service = new DefaultAfaService(afaConnector, contactsService)

      val afaReceiptDate = LocalDate.now
      val endDate        = LocalDate.now.plusYears(1).minusDays(1)

      val applicant: Company = arbitrary[Company].sample.value.copy(
        applicantAddressUkBased = true,
        applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
        applicantType = Authorised
      )

      val legalContact = genLegalContactUk.sample.value

      val expectedRequest = InitialAfa(
        id = arbitraryUkAfaId().sample.value,
        receiptDate = Some(afaReceiptDate),
        additionalInfoProvided = true,
        shareWithEuropeanCommission = Some(true),
        permissionToDestroySmallConsignments = true,
        exOfficio = Some(ExOfficio(true)),
        applicant = applicant,
        legalContact = legalContact,
        technicalContact = genContactUk.sample.value,
        endDate = endDate,
        ipRights = Seq(
          Copyright("description")
        ),
        representativeContact = RepresentativeContact(
          "name",
          "company",
          "phone",
          "email",
          Some("role"),
          UkAddress("line1", None, "town", None, "postcode"),
          Some(false)
        ),
        isRestrictedHandling = Some(false),
        secondaryLegalContact = Some(genSecondaryLegalContactInternational.sample.value),
        secondaryTechnicalContact = Some(genSecondaryTechnicalContactInternational.sample.value)
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalCompanyName,
        legalContactName,
        legalContactPhone,
        legalContactOtherPhone,
        legalContactEmail,
        legalContactAddress: UkAddress
      ) = expectedRequest.legalContact
      val Contact(
        techContactCompanyName,
        techContactName,
        techContactPhone,
        _,
        techContactEmail,
        techContactAddress: UkAddress
      ) = expectedRequest.technicalContact
      val RepresentativeContact(
        contactName,
        companyName,
        roleOrPosition,
        phone,
        email,
        address: UkAddress,
        Some(evidenceOfPowerToAct)
      ) = expectedRequest.representativeContact
      val Contact(
        secondaryTechnicalContactCompanyName,
        secondaryTechnicalContactName,
        secondaryTechnicalContactPhone,
        _,
        secondaryTechnicalContactEmail,
        secondaryTechnicalContactAddress: InternationalAddress
      ) = expectedRequest.secondaryTechnicalContact.get
      val Contact(
        secondaryLegalCompanyName,
        secondaryLegalContactName,
        secondaryLegalContactPhone,
        _,
        secondaryLegalContactEmail,
        secondaryLegalContactAddress: InternationalAddress
      ) = expectedRequest.secondaryLegalContact.get

      val userAnswers = UserAnswers(expectedRequest.id)
        .set(ApplicationReceiptDatePage, afaReceiptDate)
        .success
        .value
        .set(AdditionalInfoProvidedPage, true)
        .success
        .value
        .set(ShareWithEuropeanCommissionPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(IsExOfficioPage, true)
        .success
        .value
        .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
        .success
        .value
        .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
        .success
        .value
        .set(IsCompanyApplyingUkBasedPage, true)
        .success
        .value
        .set(CompanyApplyingUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
        .success
        .value
        .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.Authorised)
        .success
        .value
        .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
        .success
        .value
        .set(IsRepresentativeContactLegalContactPage, false)
        .success
        .value
        .set(
          ApplicantLegalContactPage,
          ApplicantLegalContact(
            legalContactName,
            legalCompanyName,
            legalContactPhone,
            legalContactOtherPhone,
            legalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(IsApplicantLegalContactUkBasedPage, true)
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(
          WhoIsTechnicalContactPage,
          TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
        )
        .success
        .value
        .set(IsTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(TechnicalContactUkAddressPage, techContactAddress)
        .success
        .value
        .set(IpRightsTypePage(0), IpRightsType.Copyright)
        .success
        .value
        .set(IpRightsDescriptionPage(0), "description")
        .success
        .value
        .set(RepresentativeDetailsPage, RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email))
        .success
        .value
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, address)
        .success
        .value
        .set(RestrictedHandlingPage, expectedRequest.isRestrictedHandling.getOrElse(false))
        .success
        .value
        .set(IsSecondaryTechnicalContactUkBasedPage, false)
        .success
        .value
        .set(
          WhoIsSecondaryTechnicalContactPage,
          TechnicalContact(
            secondaryTechnicalContactCompanyName,
            secondaryTechnicalContactName,
            secondaryTechnicalContactPhone,
            secondaryTechnicalContactEmail
          )
        )
        .success
        .value
        .set(SecondaryTechnicalContactInternationalAddressPage, secondaryTechnicalContactAddress)
        .success
        .value
        .set(IsApplicantSecondaryLegalContactUkBasedPage, false)
        .success
        .value
        .set(
          WhoIsSecondaryLegalContactPage,
          WhoIsSecondaryLegalContact(
            secondaryLegalContactName,
            secondaryLegalCompanyName,
            secondaryLegalContactPhone,
            secondaryLegalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantSecondaryLegalContactInternationalAddressPage, secondaryLegalContactAddress)
        .success
        .value

      "must not be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))

        service.canCreateAfa(userAnswers) mustBe false
      }

      "must submit data" in {

        val captorRequest: ArgumentCaptor[InitialAfa] = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
      }

      "for company that is authorised, legal and technical contact is different" - {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val afaReceiptDate = LocalDate.now
        val endDate        = LocalDate.now.plusYears(1).minusDays(1)

        val applicant: Company = arbitrary[Company].sample.value.copy(
          applicantAddressUkBased = true,
          applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
          applicantType = Authorised
        )

        val expectedRequest = InitialAfa(
          id = arbitraryUkAfaId().sample.value,
          receiptDate = Some(afaReceiptDate),
          additionalInfoProvided = true,
          shareWithEuropeanCommission = Some(true),
          permissionToDestroySmallConsignments = true,
          exOfficio = Some(ExOfficio(true)),
          applicant = applicant,
          legalContact = legalContact,
          technicalContact = genContactUk.sample.value,
          endDate = endDate,
          ipRights = Seq(
            Copyright("description")
          ),
          representativeContact = genRepresentativeContact.sample.value,
          isRestrictedHandling = Some(false),
          secondaryLegalContact = Some(genSecondaryLegalContactUk.sample.value),
          secondaryTechnicalContact = Some(genSecondaryTechnicalContactUk.sample.value)
        )

        val response = arbitrary[PublishedAfa].sample.value

        val Contact(
          legalCompanyName,
          legalContactName,
          legalContactPhone,
          legalContactOtherPhone,
          legalContactEmail,
          legalContactAddress: UkAddress
        ) = expectedRequest.legalContact
        val Contact(
          techContactCompanyName,
          techContactName,
          techContactPhone,
          _,
          techContactEmail,
          techContactAddress: UkAddress
        ) = expectedRequest.technicalContact
        val RepresentativeContact(
          contactName,
          companyName,
          roleOrPosition,
          phone,
          email,
          address: UkAddress,
          Some(evidenceOfPowerToAct)
        ) = expectedRequest.representativeContact

        val Contact(
          secondaryTechnicalContactCompanyName,
          secondaryTechnicalContactName,
          secondaryTechnicalContactPhone,
          _,
          secondaryTechnicalContactEmail,
          secondaryTechnicalContactAddress: UkAddress
        ) = expectedRequest.secondaryTechnicalContact.get
        val Contact(
          secondaryLegalCompanyName,
          secondaryLegalContactName,
          secondaryLegalContactPhone,
          _,
          secondaryLegalContactEmail,
          secondaryLegalContactAddress: UkAddress
        ) = expectedRequest.secondaryLegalContact.get

        val userAnswers = UserAnswers(expectedRequest.id)
          .set(ApplicationReceiptDatePage, afaReceiptDate)
          .success
          .value
          .set(AdditionalInfoProvidedPage, true)
          .success
          .value
          .set(ShareWithEuropeanCommissionPage, expectedRequest.permissionToDestroySmallConsignments)
          .success
          .value
          .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
          .success
          .value
          .set(IsExOfficioPage, true)
          .success
          .value
          .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
          .success
          .value
          .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
          .success
          .value
          .set(IsCompanyApplyingUkBasedPage, true)
          .success
          .value
          .set(CompanyApplyingUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.Authorised)
          .success
          .value
          .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
          .success
          .value
          .set(IsRepresentativeContactLegalContactPage, false)
          .success
          .value
          .set(
            ApplicantLegalContactPage,
            ApplicantLegalContact(
              legalContactName,
              legalCompanyName,
              legalContactPhone,
              legalContactOtherPhone,
              legalContactEmail
            )
          )
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
          .success
          .value
          .set(IsApplicantLegalContactUkBasedPage, true)
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
          .success
          .value
          .set(
            WhoIsTechnicalContactPage,
            TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
          )
          .success
          .value
          .set(IsTechnicalContactUkBasedPage, true)
          .success
          .value
          .set(TechnicalContactUkAddressPage, techContactAddress)
          .success
          .value
          .set(IpRightsTypePage(0), IpRightsType.Copyright)
          .success
          .value
          .set(IpRightsDescriptionPage(0), "description")
          .success
          .value
          .set(RepresentativeDetailsPage, RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email))
          .success
          .value
          .set(IsRepresentativeContactUkBasedPage, true)
          .success
          .value
          .set(RepresentativeContactUkAddressPage, address)
          .success
          .value
          .set(RestrictedHandlingPage, expectedRequest.isRestrictedHandling.getOrElse(false))
          .success
          .value
          .set(AddAnotherTechnicalContactPage, true)
          .success
          .value
          .set(IsSecondaryTechnicalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryTechnicalContactPage,
            TechnicalContact(
              secondaryTechnicalContactCompanyName,
              secondaryTechnicalContactName,
              secondaryTechnicalContactPhone,
              secondaryTechnicalContactEmail
            )
          )
          .success
          .value
          .set(SecondaryTechnicalContactUkAddressPage, secondaryTechnicalContactAddress)
          .success
          .value
          .set(AddAnotherLegalContactPage, true)
          .success
          .value
          .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryLegalContactPage,
            WhoIsSecondaryLegalContact(
              secondaryLegalContactName,
              secondaryLegalCompanyName,
              secondaryLegalContactPhone,
              secondaryLegalContactEmail
            )
          )
          .success
          .value
          .set(ApplicantSecondaryLegalContactUkAddressPage, secondaryLegalContactAddress)
          .success
          .value

        "must be able to create an Afa" in {

          when(contactsService.getRepresentativeContactDetails(any()))
            .thenReturn(Some(expectedRequest.representativeContact))
          when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
          when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
          when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
          when(contactsService.getSecondaryTechnicalContactDetails(any()))
            .thenReturn(expectedRequest.secondaryTechnicalContact)
          when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
          when(contactsService.getSecondaryTechnicalContactDetails(any()))
            .thenReturn(expectedRequest.secondaryTechnicalContact)

          service.canCreateAfa(userAnswers) mustBe true
        }

        "must submit data" in {

          val captorRequest: ArgumentCaptor[InitialAfa] = ArgumentCaptor.forClass(classOf[InitialAfa])

          when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
          when(contactsService.getRepresentativeContactDetails(any()))
            .thenReturn(Some(expectedRequest.representativeContact))
          when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
          when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
          when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
          when(contactsService.getSecondaryTechnicalContactDetails(any()))
            .thenReturn(expectedRequest.secondaryTechnicalContact)

          whenReady(service.submit(userAnswers)) { _ =>
            captorRequest.getValue mustEqual expectedRequest
          }
        }
      }
    }

    "for company that is rights holder" - {

      val service = new DefaultAfaService(afaConnector, contactsService)

      val afaReceiptDate = LocalDate.now
      val endDate        = LocalDate.now.plusYears(1).minusDays(1)

      val applicant: Company = arbitrary[Company].sample.value.copy(
        applicantAddressUkBased = true,
        applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
        applicantType = RightsHolder
      )

      val legalContact = genLegalContactUk.sample.value

      val expectedRequest = InitialAfa(
        id = arbitraryUkAfaId().sample.value,
        receiptDate = Some(afaReceiptDate),
        additionalInfoProvided = true,
        shareWithEuropeanCommission = Some(true),
        permissionToDestroySmallConsignments = true,
        exOfficio = Some(ExOfficio(true)),
        applicant = applicant,
        legalContact = legalContact,
        technicalContact = genContactUk.sample.value,
        endDate = endDate,
        ipRights = Seq(
          Copyright("description")
        ),
        representativeContact = genRepresentativeContact.sample.value,
        isRestrictedHandling = Some(false),
        secondaryLegalContact = Some(genSecondaryLegalContactUk.sample.value),
        secondaryTechnicalContact = Some(genSecondaryTechnicalContactUk.sample.value)
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalCompanyName,
        legalContactName,
        legalContactPhone,
        legalContactOtherPhone,
        legalContactEmail,
        legalContactAddress: UkAddress
      ) = expectedRequest.legalContact
      val Contact(
        techContactCompanyName,
        techContactName,
        techContactPhone,
        _,
        techContactEmail,
        techContactAddress: UkAddress
      ) = expectedRequest.technicalContact
      val RepresentativeContact(
        contactName,
        companyName,
        roleOrPosition,
        phone,
        email,
        address: UkAddress,
        Some(evidenceOfPowerToAct)
      ) = expectedRequest.representativeContact

      val Contact(
        secondaryTechnicalContactCompanyName,
        secondaryTechnicalContactName,
        secondaryTechnicalContactPhone,
        _,
        secondaryTechnicalContactEmail,
        secondaryTechnicalContactAddress: UkAddress
      ) = expectedRequest.secondaryTechnicalContact.get
      val Contact(
        secondaryLegalCompanyName,
        secondaryLegalContactName,
        secondaryLegalContactPhone,
        _,
        secondaryLegalContactEmail,
        secondaryLegalContactAddress: UkAddress
      ) = expectedRequest.secondaryLegalContact.get

      val userAnswers = UserAnswers(expectedRequest.id)
        .set(ApplicationReceiptDatePage, afaReceiptDate)
        .success
        .value
        .set(AdditionalInfoProvidedPage, true)
        .success
        .value
        .set(ShareWithEuropeanCommissionPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(IsExOfficioPage, true)
        .success
        .value
        .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
        .success
        .value
        .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
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
        .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
        .success
        .value
        .set(IsRepresentativeContactLegalContactPage, false)
        .success
        .value
        .set(
          ApplicantLegalContactPage,
          ApplicantLegalContact(
            legalContactName,
            legalCompanyName,
            legalContactPhone,
            legalContactOtherPhone,
            legalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(IsApplicantLegalContactUkBasedPage, true)
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(
          WhoIsTechnicalContactPage,
          TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
        )
        .success
        .value
        .set(IsTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(TechnicalContactUkAddressPage, techContactAddress)
        .success
        .value
        .set(IpRightsTypePage(0), IpRightsType.Copyright)
        .success
        .value
        .set(IpRightsDescriptionPage(0), "description")
        .success
        .value
        .set(RepresentativeDetailsPage, RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email))
        .success
        .value
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, address)
        .success
        .value
        .set(RestrictedHandlingPage, expectedRequest.isRestrictedHandling.getOrElse(false))
        .success
        .value
        .set(SelectTechnicalContactPage, ContactOptions.SomeoneElse)
        .success
        .value
        .set(SelectOtherTechnicalContactPage, ContactOptions.SomeoneElse)
        .success
        .value
        .set(SelectTechnicalContactPage, ContactOptions.SomeoneElse)
        .success
        .value
        .set(AddAnotherTechnicalContactPage, true)
        .success
        .value
        .set(IsSecondaryTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(
          WhoIsSecondaryTechnicalContactPage,
          TechnicalContact(
            secondaryTechnicalContactCompanyName,
            secondaryTechnicalContactName,
            secondaryTechnicalContactPhone,
            secondaryTechnicalContactEmail
          )
        )
        .success
        .value
        .set(SecondaryTechnicalContactUkAddressPage, secondaryTechnicalContactAddress)
        .success
        .value
        .set(AddAnotherLegalContactPage, true)
        .success
        .value
        .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
        .success
        .value
        .set(
          WhoIsSecondaryLegalContactPage,
          WhoIsSecondaryLegalContact(
            secondaryLegalContactName,
            secondaryLegalCompanyName,
            secondaryLegalContactPhone,
            secondaryLegalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantSecondaryLegalContactUkAddressPage, secondaryLegalContactAddress)
        .success
        .value

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
      }
    }

    "for company that is a  collective body" - {

      val service = new DefaultAfaService(afaConnector, contactsService)

      val afaReceiptDate = LocalDate.now
      val endDate        = LocalDate.now.plusYears(1).minusDays(1)

      val applicant: Company = arbitrary[Company].sample.value.copy(
        applicantAddressUkBased = true,
        applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
        applicantType = CollectiveBody
      )

      val legalContact = genLegalContactUk.sample.value

      val expectedRequest = InitialAfa(
        id = arbitraryUkAfaId().sample.value,
        receiptDate = Some(afaReceiptDate),
        additionalInfoProvided = true,
        shareWithEuropeanCommission = Some(false),
        permissionToDestroySmallConsignments = true,
        exOfficio = Some(ExOfficio(true)),
        applicant = applicant,
        legalContact = legalContact,
        technicalContact = genContactUk.sample.value,
        endDate = endDate,
        ipRights = Seq(
          Copyright("description")
        ),
        representativeContact = genRepresentativeContact.sample.value,
        isRestrictedHandling = Some(true),
        secondaryLegalContact = Some(genSecondaryLegalContactUk.sample.value),
        secondaryTechnicalContact = Some(genSecondaryTechnicalContactUk.sample.value)
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalCompanyName,
        legalContactName,
        legalContactPhone,
        legalContactOtherPhone,
        legalContactEmail,
        legalContactAddress: UkAddress
      ) = expectedRequest.legalContact
      val Contact(
        techContactCompanyName,
        techContactName,
        techContactPhone,
        _,
        techContactEmail,
        techContactAddress: UkAddress
      ) = expectedRequest.technicalContact
      val RepresentativeContact(
        contactName,
        companyName,
        roleOrPosition,
        phone,
        email,
        address: UkAddress,
        Some(evidenceOfPowerToAct)
      ) = expectedRequest.representativeContact
      val Contact(
        secondaryTechnicalContactCompanyName,
        secondaryTechnicalContactName,
        secondaryTechnicalContactPhone,
        _,
        secondaryTechnicalContactEmail,
        secondaryTechnicalContactAddress: UkAddress
      ) = expectedRequest.secondaryTechnicalContact.get
      val Contact(
        secondaryLegalCompanyName,
        secondaryLegalContactName,
        secondaryLegalContactPhone,
        _,
        secondaryLegalContactEmail,
        secondaryLegalContactAddress: UkAddress
      ) = expectedRequest.secondaryLegalContact.get

      val userAnswers = UserAnswers(expectedRequest.id)
        .set(ApplicationReceiptDatePage, afaReceiptDate)
        .success
        .value
        .set(AdditionalInfoProvidedPage, true)
        .success
        .value
        .set(ShareWithEuropeanCommissionPage, expectedRequest.shareWithEuropeanCommission.get)
        .success
        .value
        .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(IsExOfficioPage, true)
        .success
        .value
        .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
        .success
        .value
        .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
        .success
        .value
        .set(IsCompanyApplyingUkBasedPage, true)
        .success
        .value
        .set(CompanyApplyingUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
        .success
        .value
        .set(CompanyApplyingIsRightsHolderPage, CompanyApplyingIsRightsHolder.CollectiveBody)
        .success
        .value
        .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
        .success
        .value
        .set(IsRepresentativeContactLegalContactPage, false)
        .success
        .value
        .set(
          ApplicantLegalContactPage,
          ApplicantLegalContact(
            legalContactName,
            legalCompanyName,
            legalContactPhone,
            legalContactOtherPhone,
            legalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(IsApplicantLegalContactUkBasedPage, true)
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(
          WhoIsTechnicalContactPage,
          TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
        )
        .success
        .value
        .set(IsTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(TechnicalContactUkAddressPage, techContactAddress)
        .success
        .value
        .set(IpRightsTypePage(0), IpRightsType.Copyright)
        .success
        .value
        .set(IpRightsDescriptionPage(0), "description")
        .success
        .value
        .set(RepresentativeDetailsPage, RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email))
        .success
        .value
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, address)
        .success
        .value
        .set(RestrictedHandlingPage, expectedRequest.isRestrictedHandling.get)
        .success
        .value
        .set(AddAnotherTechnicalContactPage, true)
        .success
        .value
        .set(IsSecondaryTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(
          WhoIsSecondaryTechnicalContactPage,
          TechnicalContact(
            secondaryTechnicalContactCompanyName,
            secondaryTechnicalContactName,
            secondaryTechnicalContactPhone,
            secondaryTechnicalContactEmail
          )
        )
        .success
        .value
        .set(SecondaryTechnicalContactUkAddressPage, secondaryTechnicalContactAddress)
        .success
        .value
        .set(AddAnotherLegalContactPage, true)
        .success
        .value
        .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
        .success
        .value
        .set(
          WhoIsSecondaryLegalContactPage,
          WhoIsSecondaryLegalContact(
            secondaryLegalContactName,
            secondaryLegalCompanyName,
            secondaryLegalContactPhone,
            secondaryLegalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantSecondaryLegalContactUkAddressPage, secondaryLegalContactAddress)
        .success
        .value

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
      }
    }

    "for an application with all types of IP right" - {

      val service = new DefaultAfaService(afaConnector, contactsService)

      val arbitraryDate = LocalDate.now
      val endDate       = LocalDate.now.plusYears(1).minusDays(1)

      val applicant: Company = arbitrary[Company].sample.value.copy(
        applicantAddressUkBased = true,
        applicantContactAddress = UkAddress("street", None, "town", None, "postcode"),
        applicantType = RightsHolder
      )

      val legalContact = genLegalContactUk.sample.value

      val expectedRequest = InitialAfa(
        id = arbitraryUkAfaId().sample.value,
        receiptDate = Some(arbitraryDate),
        additionalInfoProvided = true,
        shareWithEuropeanCommission = Some(true),
        permissionToDestroySmallConsignments = true,
        exOfficio = Some(ExOfficio(true)),
        applicant = applicant,
        legalContact = legalContact,
        technicalContact = legalContact,
        endDate = endDate,
        ipRights = Seq(
          Copyright("copyright description"),
          Design("design number", arbitraryDate, "design description"),
          Patent("patent number", arbitraryDate, "patent description"),
          PlantVariety("plant variety description"),
          GeographicalIndication("geographical description"),
          SemiconductorTopography("semiconductor description"),
          Trademark(
            "trademark number",
            arbitraryDate,
            Some("trademark brand"),
            "trademark description",
            Seq(NiceClassId.fromInt(1).value, NiceClassId.fromInt(2).value)
          ),
          Trademark(
            "trademark 2 number",
            arbitraryDate,
            Some("trademark 2 brand"),
            "trademark 2 description",
            Seq(NiceClassId.fromInt(3).value)
          ),
          SupplementaryProtectionCertificate(
            "medicinalProducts",
            "certificate number",
            arbitraryDate,
            "certificate description"
          )
        ),
        representativeContact = genRepresentativeContact.sample.value,
        isRestrictedHandling = Some(false),
        secondaryLegalContact = Some(genSecondaryLegalContactInternational.sample.value),
        secondaryTechnicalContact = Some(genSecondaryTechnicalContactInternational.sample.value)
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalCompanyName,
        legalContactName,
        legalContactPhone,
        legalContactOtherPhone,
        legalContactEmail,
        legalContactAddress: UkAddress
      ) = expectedRequest.legalContact
      val Contact(
        techContactCompanyName,
        techContactName,
        techContactPhone,
        _,
        techContactEmail,
        techContactAddress: UkAddress
      ) = expectedRequest.technicalContact
      val RepresentativeContact(
        contactName,
        companyName,
        roleOrPosition,
        phone,
        email,
        address: UkAddress,
        Some(evidenceOfPowerToAct)
      ) = expectedRequest.representativeContact

      val Contact(
        secondaryTechnicalContactCompanyName,
        secondaryTechnicalContactName,
        secondaryTechnicalContactPhone,
        _,
        secondaryTechnicalContactEmail,
        secondaryTechnicalContactAddress: InternationalAddress
      ) = expectedRequest.secondaryTechnicalContact.get
      val Contact(
        secondaryLegalCompanyName,
        secondaryLegalContactName,
        secondaryLegalContactPhone,
        _,
        secondaryLegalContactEmail,
        secondaryLegalContactAddress: InternationalAddress
      ) = expectedRequest.secondaryLegalContact.get

      val userAnswers = UserAnswers(expectedRequest.id)
        .set(ApplicationReceiptDatePage, arbitraryDate)
        .success
        .value
        .set(AdditionalInfoProvidedPage, true)
        .success
        .value
        .set(ShareWithEuropeanCommissionPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(PermissionToDestroySmallConsignmentsPage, expectedRequest.permissionToDestroySmallConsignments)
        .success
        .value
        .set(IsExOfficioPage, true)
        .success
        .value
        .set(WantsOneYearRightsProtectionPage, expectedRequest.exOfficio.value.wantsOneYearProtection)
        .success
        .value
        .set(CompanyApplyingPage, CompanyApplying(applicant.name, applicant.acronym))
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
        .set(EvidenceOfPowerToActPage, evidenceOfPowerToAct)
        .success
        .value
        .set(IsRepresentativeContactLegalContactPage, false)
        .success
        .value
        .set(
          ApplicantLegalContactPage,
          ApplicantLegalContact(
            legalContactName,
            legalCompanyName,
            legalContactPhone,
            legalContactOtherPhone,
            legalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(IsApplicantLegalContactUkBasedPage, true)
        .success
        .value
        .set(ApplicantLegalContactUkAddressPage, legalContactAddress)
        .success
        .value
        .set(
          WhoIsTechnicalContactPage,
          TechnicalContact(techContactCompanyName, techContactName, techContactPhone, techContactEmail)
        )
        .success
        .value
        .set(IsTechnicalContactUkBasedPage, true)
        .success
        .value
        .set(TechnicalContactUkAddressPage, techContactAddress)
        .success
        .value
        .set(IpRightsTypePage(0), IpRightsType.Copyright)
        .success
        .value
        .set(IpRightsDescriptionPage(0), "copyright description")
        .success
        .value
        .set(IpRightsTypePage(1), IpRightsType.Design)
        .success
        .value
        .set(IpRightsRegistrationNumberPage(1), "design number")
        .success
        .value
        .set(IpRightsRegistrationEndPage(1), arbitraryDate)
        .success
        .value
        .set(IpRightsDescriptionPage(1), "design description")
        .success
        .value
        .set(IpRightsTypePage(2), IpRightsType.Patent)
        .success
        .value
        .set(IpRightsRegistrationNumberPage(2), "patent number")
        .success
        .value
        .set(IpRightsRegistrationEndPage(2), arbitraryDate)
        .success
        .value
        .set(IpRightsDescriptionPage(2), "patent description")
        .success
        .value
        .set(IpRightsTypePage(3), IpRightsType.PlantVariety)
        .success
        .value
        .set(IpRightsDescriptionPage(3), "plant variety description")
        .success
        .value
        .set(IpRightsTypePage(4), IpRightsType.GeographicalIndication)
        .success
        .value
        .set(IpRightsDescriptionPage(4), "geographical description")
        .success
        .value
        .set(IpRightsTypePage(5), IpRightsType.SemiconductorTopography)
        .success
        .value
        .set(IpRightsDescriptionPage(5), "semiconductor description")
        .success
        .value
        .set(IpRightsTypePage(6), IpRightsType.Trademark)
        .success
        .value
        .set(IpRightsRegistrationNumberPage(6), "trademark number")
        .success
        .value
        .set(IpRightsRegistrationEndPage(6), arbitraryDate)
        .success
        .value
        .set(
          IpRightsDescriptionWithBrandPage(6),
          IpRightsDescriptionWithBrand("trademark brand", "trademark description")
        )
        .success
        .value
        .set(IpRightsNiceClassPage(6, 0), NiceClassId.fromInt(1).value)
        .success
        .value
        .set(IpRightsNiceClassPage(6, 1), NiceClassId.fromInt(2).value)
        .success
        .value
        .set(IpRightsTypePage(7), IpRightsType.Trademark)
        .success
        .value
        .set(IpRightsRegistrationNumberPage(7), "trademark 2 number")
        .success
        .value
        .set(IpRightsRegistrationEndPage(7), arbitraryDate)
        .success
        .value
        .set(
          IpRightsDescriptionWithBrandPage(7),
          IpRightsDescriptionWithBrand("trademark 2 brand", "trademark 2 description")
        )
        .success
        .value
        .set(IpRightsNiceClassPage(7, 0), NiceClassId.fromInt(3).value)
        .success
        .value
        .set(IpRightsTypePage(8), IpRightsType.SupplementaryProtectionCertificate)
        .success
        .value
        .set(
          IpRightsSupplementaryProtectionCertificateTypePage(8),
          IpRightsSupplementaryProtectionCertificateType.Medicinal
        )
        .success
        .value
        .set(IpRightsRegistrationNumberPage(8), "certificate number")
        .success
        .value
        .set(IpRightsRegistrationEndPage(8), arbitraryDate)
        .success
        .value
        .set(IpRightsDescriptionPage(8), "certificate description")
        .success
        .value
        .set(RepresentativeDetailsPage, RepresentativeDetails(contactName, companyName, roleOrPosition, phone, email))
        .success
        .value
        .set(IsRepresentativeContactUkBasedPage, true)
        .success
        .value
        .set(RepresentativeContactUkAddressPage, address)
        .success
        .value
        .set(RestrictedHandlingPage, expectedRequest.isRestrictedHandling.getOrElse(false))
        .success
        .value
        .set(AddAnotherTechnicalContactPage, true)
        .success
        .value
        .set(IsSecondaryTechnicalContactUkBasedPage, false)
        .success
        .value
        .set(
          WhoIsSecondaryTechnicalContactPage,
          TechnicalContact(
            secondaryTechnicalContactCompanyName,
            secondaryTechnicalContactName,
            secondaryTechnicalContactPhone,
            secondaryTechnicalContactEmail
          )
        )
        .success
        .value
        .set(SecondaryTechnicalContactInternationalAddressPage, secondaryTechnicalContactAddress)
        .success
        .value
        .set(AddAnotherLegalContactPage, true)
        .success
        .value
        .set(IsApplicantSecondaryLegalContactUkBasedPage, false)
        .success
        .value
        .set(
          WhoIsSecondaryLegalContactPage,
          WhoIsSecondaryLegalContact(
            secondaryLegalContactName,
            secondaryLegalCompanyName,
            secondaryLegalContactPhone,
            secondaryLegalContactEmail
          )
        )
        .success
        .value
        .set(ApplicantSecondaryLegalContactInternationalAddressPage, secondaryLegalContactAddress)
        .success
        .value

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit the correct data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(expectedRequest.secondaryLegalContact)
        when(contactsService.getSecondaryTechnicalContactDetails(any()))
          .thenReturn(expectedRequest.secondaryTechnicalContact)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
      }
    }

    "when Is Ex-officio is true but Wants One Year Protection is not answered" - {

      "must not be able to create an AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val userAnswersId: AfaId = arbitrary[AfaId].sample.value

        val userAnswers = UserAnswers(userAnswersId)
          .set(PermissionToDestroySmallConsignmentsPage, true)
          .success
          .value
          .set(IsExOfficioPage, true)
          .success
          .value
          .set(CompanyApplyingPage, CompanyApplying("name", None))
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
          .set(EvidenceOfPowerToActPage, false)
          .success
          .value
          .set(IsRepresentativeContactLegalContactPage, false)
          .success
          .value
          .set(ApplicantLegalContactPage, ApplicantLegalContact("name", "company", "phone", None, "email"))
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(IsApplicantLegalContactUkBasedPage, true)
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(IpRightsTypePage(0), IpRightsType.Copyright)
          .success
          .value
          .set(IpRightsDescriptionPage(0), "description")
          .success
          .value
          .set(
            RepresentativeDetailsPage,
            RepresentativeDetails("contactName", "companyName", "phone", "email", Some("roleOrPosition"))
          )
          .success
          .value
          .set(IsRepresentativeContactUkBasedPage, true)
          .success
          .value
          .set(RepresentativeContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(AddAnotherTechnicalContactPage, true)
          .success
          .value
          .set(IsSecondaryTechnicalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryTechnicalContactPage,
            TechnicalContact(
              "secondaryTechnicalContactName",
              "secondaryTechnicalCompanyName",
              "secondaryTechnicalContactPhone",
              "secondaryTechnicalContactEmail"
            )
          )
          .success
          .value
          .set(SecondaryTechnicalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(AddAnotherLegalContactPage, true)
          .success
          .value
          .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryLegalContactPage,
            WhoIsSecondaryLegalContact(
              "secondaryLegalContactName",
              "secondaryLegalCompanyName",
              "secondaryLegalContactPhone",
              "secondaryLegalContactEmail"
            )
          )
          .success
          .value
          .set(ApplicantSecondaryLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value

        service.canCreateAfa(userAnswers) mustBe false
      }
    }

    "when Evidence Of Power To Act is false" - {

      "must not be able to create an AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val userAnswersId: AfaId = arbitrary[AfaId].sample.value

        val userAnswers = UserAnswers(userAnswersId)
          .set(PermissionToDestroySmallConsignmentsPage, true)
          .success
          .value
          .set(IsExOfficioPage, true)
          .success
          .value
          .set(WantsOneYearRightsProtectionPage, true)
          .success
          .value
          .set(CompanyApplyingPage, CompanyApplying("name", None))
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
          .set(EvidenceOfPowerToActPage, false)
          .success
          .value
          .set(IsRepresentativeContactLegalContactPage, false)
          .success
          .value
          .set(ApplicantLegalContactPage, ApplicantLegalContact("name", "company", "phone", None, "email"))
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(IsApplicantLegalContactUkBasedPage, true)
          .success
          .value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(IpRightsTypePage(0), IpRightsType.Copyright)
          .success
          .value
          .set(IpRightsDescriptionPage(0), "description")
          .success
          .value
          .set(AddAnotherTechnicalContactPage, true)
          .success
          .value
          .set(IsSecondaryTechnicalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryTechnicalContactPage,
            TechnicalContact(
              "secondaryTechnicalContactName",
              "secondaryTechnicalCompanyName",
              "secondaryTechnicalContactPhone",
              "secondaryTechnicalContactEmail"
            )
          )
          .success
          .value
          .set(SecondaryTechnicalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value
          .set(AddAnotherLegalContactPage, true)
          .success
          .value
          .set(IsApplicantSecondaryLegalContactUkBasedPage, true)
          .success
          .value
          .set(
            WhoIsSecondaryLegalContactPage,
            WhoIsSecondaryLegalContact(
              "secondaryLegalContactName",
              "secondaryLegalCompanyName",
              "secondaryLegalContactPhone",
              "secondaryLegalContactEmail"
            )
          )
          .success
          .value
          .set(ApplicantSecondaryLegalContactUkAddressPage, UkAddress("street", None, "town", None, "postcode"))
          .success
          .value

        service.canCreateAfa(userAnswers) mustBe false
      }
    }

  }
}
