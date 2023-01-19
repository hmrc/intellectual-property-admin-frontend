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
import models.{AfaId, ApplicantLegalContact, CompanyApplying, CompanyApplyingIsRightsHolder, IpRightsDescriptionWithBrand, IpRightsSupplementaryProtectionCertificateType, IpRightsType, NiceClassId, RepresentativeDetails, TechnicalContact, UkAddress, UserAnswers, afa}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalacheck.Arbitrary._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AfaServiceSpec
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

  val userAnswersBaseAfaId: AfaId  = AfaId.fromString("UK20190123").get
  val userAnswersBase: UserAnswers = UserAnswers(userAnswersBaseAfaId)
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
    .set(ApplicantLegalContactPage, ApplicantLegalContact("name", "companyName", "phone", None, "email"))
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
          secondaryLegalContact = None,
          secondaryTechnicalContact = None
        )

        val Contact(
          legalContactCompanyName,
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
                legalContactCompanyName,
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

      val expectedRequest = InitialAfa(
        id = arbitraryUkAfaId().sample.value,
        receiptDate = Some(afaReceiptDate),
        additionalInfoProvided = true,
        shareWithEuropeanCommission = Some(true),
        permissionToDestroySmallConsignments = true,
        exOfficio = Some(ExOfficio(true)),
        applicant = applicant,
        legalContact = genLegalContactUk.sample.value,
        technicalContact = genContactUk.sample.value,
        endDate = endDate,
        ipRights = Seq(
          Copyright("description")
        ),
        representativeContact = RepresentativeContact(
          "name",
          "companyName",
          "phone",
          "email",
          Some("role"),
          UkAddress("line1", None, "town", None, "postcode"),
          Some(false)
        ),
        isRestrictedHandling = Some(false),
        secondaryLegalContact = None,
        secondaryTechnicalContact = None
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalContactCompanyName,
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
            legalContactCompanyName,
            legalContactPhone,
            legalContactOtherPhone,
            legalContactEmail
          )
        )
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
        .set(AddAnotherLegalContactPage, false)
        .success
        .value
        .remove(WhoIsSecondaryLegalContactPage)
        .success
        .value
        .remove(IsApplicantSecondaryLegalContactUkBasedPage)
        .success
        .value
        .remove(ApplicantSecondaryLegalContactInternationalAddressPage)
        .success
        .value
        .remove(ApplicantSecondaryLegalContactUkAddressPage)
        .success
        .value
        .set(AddAnotherTechnicalContactPage, false)
        .success
        .value
        .remove(WhoIsSecondaryTechnicalContactPage)
        .success
        .value
        .remove(IsSecondaryTechnicalContactUkBasedPage)
        .success
        .value
        .remove(SecondaryTechnicalContactInternationalAddressPage)
        .success
        .value
        .remove(SecondaryTechnicalContactUkAddressPage)
        .success
        .value

      "must not be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        service.canCreateAfa(userAnswers) mustBe false
      }

      "must submit data" in {

        val captorRequest: ArgumentCaptor[InitialAfa] = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
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
        secondaryLegalContact = None,
        secondaryTechnicalContact = None
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalContactCompanyName,
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
            legalContactCompanyName,
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
        .set(AddAnotherLegalContactPage, false)
        .success
        .value
        .remove(WhoIsSecondaryLegalContactPage)
        .success
        .value
        .remove(IsApplicantSecondaryLegalContactUkBasedPage)
        .success
        .value
        .remove(ApplicantSecondaryLegalContactInternationalAddressPage)
        .success
        .value
        .remove(ApplicantSecondaryLegalContactUkAddressPage)
        .success
        .value
        .set(AddAnotherTechnicalContactPage, false)
        .success
        .value
        .remove(WhoIsSecondaryTechnicalContactPage)
        .success
        .value
        .remove(IsSecondaryTechnicalContactUkBasedPage)
        .success
        .value
        .remove(SecondaryTechnicalContactInternationalAddressPage)
        .success
        .value
        .remove(SecondaryTechnicalContactUkAddressPage)
        .success
        .value

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit data" in {

        val captorRequest: ArgumentCaptor[InitialAfa] = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
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
        secondaryLegalContact = None,
        secondaryTechnicalContact = None
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalContactCompanyName,
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
            legalContactCompanyName,
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

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

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
        secondaryLegalContact = None,
        secondaryTechnicalContact = None
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalContactCompanyName,
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
            legalContactCompanyName,
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

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))

        service.canCreateAfa(userAnswers) mustBe true
      }

      "must submit data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

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
        secondaryLegalContact = None,
        secondaryTechnicalContact = None
      )

      val response = arbitrary[PublishedAfa].sample.value

      val Contact(
        legalContactCompanyName,
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
            legalContactCompanyName,
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

      "must be able to create an Afa" in {

        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        service.canCreateAfa(userAnswers) mustBe true
      }

      "the medicinal certificate type must save as medicinalProducts when submitting" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue.ipRights(8) mustEqual
            SupplementaryProtectionCertificate(
              "medicinalProducts",
              "certificate number",
              arbitraryDate,
              "certificate description"
            )
        }
      }

      "must submit the correct data" in {

        val captorRequest = ArgumentCaptor.forClass(classOf[InitialAfa])

        when(afaConnector.submit(captorRequest.capture)(any(), any())) thenReturn Future.successful(response)
        when(contactsService.getRepresentativeContactDetails(any()))
          .thenReturn(Some(expectedRequest.representativeContact))
        when(contactsService.getLegalContactDetails(any())).thenReturn(Some(expectedRequest.legalContact))
        when(contactsService.getTechnicalContactDetails(any())).thenReturn(Some(expectedRequest.technicalContact))
        when(contactsService.getSecondaryLegalContactDetails(any())).thenReturn(None)
        when(contactsService.getSecondaryTechnicalContactDetails(any())).thenReturn(None)

        whenReady(service.submit(userAnswers)) { _ =>
          captorRequest.getValue mustEqual expectedRequest
        }
      }
    }

    "when some required data is not present" - {

      "must not be able to create an AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val userAnswersId: AfaId = arbitrary[AfaId].sample.value

        val userAnswers = UserAnswers(userAnswersId)

        service.canCreateAfa(userAnswers) mustBe false
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
          .set(ApplicantLegalContactPage, ApplicantLegalContact("name", "companyName", "phone", None, "email"))
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
          .set(ApplicantLegalContactPage, ApplicantLegalContact("name", "companyName", "phone", None, "email"))
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

        service.canCreateAfa(userAnswers) mustBe false
      }
    }

    "when calling .removeDraft" - {

      "must remove the draft AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        when(afaConnector.removeDraft(userAnswersBaseAfaId)) thenReturn Future.successful(Some(userAnswersBase))

        val result: Future[Option[UserAnswers]] = service.removeDraft(userAnswersBaseAfaId)
        result.futureValue.value mustBe userAnswersBase

        whenReady(result) { _ =>
          verify(afaConnector, times(1)).removeDraft(eqTo(userAnswersBaseAfaId))(any())
        }
      }
    }

    "when calling .draftList" - {

      "must get the list of draft AFAs" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val afaId2: AfaId               = AfaId.fromString("UK20190127").get
        val secondDraftAfa: UserAnswers = userAnswersBase
          .copy(id = afaId2)
          .set(CompanyApplyingPage, CompanyApplying("name", None))
          .success
          .value

        val drafts: List[UserAnswers] = List(userAnswersBase, secondDraftAfa)

        when(afaConnector.draftList(any())) thenReturn Future.successful(drafts)

        val result: Future[List[UserAnswers]] = service.draftList(any())
        result.futureValue mustBe drafts

        whenReady(result) { _ =>
          verify(afaConnector, times(1)).draftList(any())
        }
      }
    }

    "when calling .getDraft" - {

      "must get the draft AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        val returnJson = Json.toJson(userAnswersBase).as[JsObject]

        when(afaConnector.getDraft(userAnswersBaseAfaId)) thenReturn Future.successful(Some(returnJson))

        val result = service.getDraft(userAnswersBaseAfaId)
        result.futureValue.value mustBe userAnswersBase

        whenReady(result) { _ =>
          verify(afaConnector, times(1)).getDraft(eqTo(userAnswersBaseAfaId))(any())
        }
      }
    }

    "when calling .set" - {

      "must perform a set to save the draft AFA" in {

        val service = new DefaultAfaService(afaConnector, contactsService)

        when(afaConnector.set(userAnswersBaseAfaId, userAnswersBase)) thenReturn Future.successful(true)

        val result = service.set(userAnswersBase)
        result.futureValue mustBe true
        whenReady(result) { _ =>
          verify(afaConnector, times(1)).set(eqTo(userAnswersBaseAfaId), eqTo(userAnswersBase))(any())
        }

      }
    }

  }
}
