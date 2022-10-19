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

package controllers

import java.time.LocalDate

import base.SpecBase
import models.CompanyApplyingIsRightsHolder.Authorised
import models._
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.TryValues
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService
import utils.CheckYourAnswersHelper
import viewmodels.{AnswerSection, SectionLink}
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with TryValues with MockitoSugar {

  "Check Your Answers Controller" must {

    val afaId = userAnswersId

    val companyApplying = CompanyApplying("company name", None)

    val baseAnswers = UserAnswers(afaId).set(CompanyApplyingPage, companyApplying).success.value

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "return OK for a GET when company applying has not yet been answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "include an application section if data exists for it" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val shareWithEuropean: Boolean = false
      val userAnswers =
        UserAnswers(afaId)
          .set(ApplicationReceiptDatePage, LocalDate.now).success.value
          .set(AdditionalInfoProvidedPage, shareWithEuropean).success.value
          .set(ShareWithEuropeanCommissionPage, true).success.value
          .set(PermissionToDestroySmallConsignmentsPage, true).success.value
          .set(IsExOfficioPage, true).success.value
          .set(WantsOneYearRightsProtectionPage, true).success.value
          .set(CompanyApplyingPage, CompanyApplying("Applicant Name", Some("AN"))).success.value
          .set(CompanyApplyingIsRightsHolderPage, Authorised).success.value
          .set(RestrictedHandlingPage, !shareWithEuropean).success.value


      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          Some("checkYourAnswers.application"),
          Seq(
            cyaHelper.applicationReceiptDate.value,
            cyaHelper.companyApplyingName.value,
            cyaHelper.companyAcronym.value,
            cyaHelper.companyApplyingIsRightsHolder.value
          )
        ),
        cyaHelper.ipRightsSection(false).value,
        cyaHelper.additionalInformationSection.value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided,
          companyApplying.name)(messages).toString

      application.stop()
    }

    "include a Legal Contact section if data exists for it" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val userAnswers =
        UserAnswers(afaId)
          .set(IsRepresentativeContactLegalContactPage, false).success.value
          .set(ApplicantLegalContactPage, ApplicantLegalContact("companyName", "RH name", "telephone", Some("other phone"), "email")).success.value
          .set(IsApplicantLegalContactUkBasedPage, true).success.value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("line1", None, "town", None, "post code")).success.value
          .set(CompanyApplyingPage, companyApplying).success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          Some("checkYourAnswers.legalContact"),
          Seq(
            cyaHelper.applicantLegalContactName.value,
            cyaHelper.applicantLegalCompanyName.value,
            cyaHelper.applicantLegalContactTelephone.value,
            cyaHelper.applicantLegalContactOtherTelephone.value,
            cyaHelper.applicantLegalContactEmail.value,
            cyaHelper.isApplicantLegalContactUkBased.value,
            cyaHelper.applicantLegalContactUkAddress.value
          )
        ),
        AnswerSection(
          None,
          Seq(),
          Some(SectionLink(controllers.routes.WhoIsSecondaryLegalContactController.onPageLoad(CheckMode,
            afaId).url, "checkYourAnswers.addAnotherLegalContact"))
        ),
        cyaHelper.ipRightsSection(false).value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided, companyApplying.name)(messages).toString

      application.stop()
    }

    "include a secondary Legal Contact section if data exists for it" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val userAnswers =
        UserAnswers(afaId)
          .set(IsRepresentativeContactLegalContactPage, false).success.value
          .set(ApplicantLegalContactPage, ApplicantLegalContact("companyName", "RH name","telephone", Some("other phone"), "email")).success.value
          .set(IsApplicantLegalContactUkBasedPage, true).success.value
          .set(ApplicantLegalContactUkAddressPage, UkAddress("line1", None, "town", None, "post code")).success.value
          .set(WhoIsSecondaryLegalContactPage, WhoIsSecondaryLegalContact("companyName", "SL name", "phone", "email@server")).success.value
          .set(IsApplicantSecondaryLegalContactUkBasedPage, true).success.value
          .set(ApplicantSecondaryLegalContactUkAddressPage, UkAddress("line1 s", None, "town s", None, "post code s")).success.value
          .set(CompanyApplyingPage, companyApplying).success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          Some("checkYourAnswers.legalContact"),
          Seq(
            cyaHelper.applicantLegalContactName.value,
            cyaHelper.applicantLegalCompanyName.value,
            cyaHelper.applicantLegalContactTelephone.value,
            cyaHelper.applicantLegalContactOtherTelephone.value,
            cyaHelper.applicantLegalContactEmail.value,
            cyaHelper.isApplicantLegalContactUkBased.value,
            cyaHelper.applicantLegalContactUkAddress.value
          )
        ),
        AnswerSection(
          Some("checkYourAnswers.secondaryLegalContact"),
          Seq(
            cyaHelper.applicantSecondaryLegalContactName.value,
            cyaHelper.applicantSecondaryLegalContactCompanyName.value,
            cyaHelper.applicantSecondaryLegalContactTelephone.value,
            cyaHelper.applicantSecondaryLegalContactEmail.value,
            cyaHelper.isApplicantSecondaryLegalContactUkBased.value,
            cyaHelper.applicantSecondaryLegalContactUkAddress.value
          ),
          Some(SectionLink(controllers.routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, "legal").url, "checkYourAnswers.removeSecondaryLegalContact"))
        ),
        cyaHelper.ipRightsSection(false).value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided,
          companyApplying.name)(messages).toString

      application.stop()
    }

    "include a technical contact section if data exists for it" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val userAnswers =
        UserAnswers(afaId)
        .set(WhoIsTechnicalContactPage, TechnicalContact("company name", "contact name", "telephone", "email")).success.value
        .set(IsTechnicalContactUkBasedPage, true).success.value
        .set(TechnicalContactUkAddressPage, UkAddress("line1", None, "town", None, "postcode")).success.value
        .set(TechnicalContactInternationalAddressPage, InternationalAddress("line1", None, "town", "country", None)).success.value
        .set(CompanyApplyingPage, companyApplying).success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          Some("checkYourAnswers.technicalContact"),
          Seq(
            cyaHelper.whoIsTechnicalContactName.value,
            cyaHelper.whoIsTechnicalContactCompany.value,
            cyaHelper.whoIsTechnicalContactPhone.value,
            cyaHelper.whoIsTechnicalContactEmail.value,
            cyaHelper.isTechnicalContactUkBased.value,
            cyaHelper.technicalContactUkAddress.value,
            cyaHelper.technicalContactInternationalAddress.value
          )
        ),
        AnswerSection(
          None,
          Seq(),
          Some(SectionLink(controllers.routes.SelectOtherTechnicalContactController.onPageLoad(CheckMode,
            afaId).url, "checkYourAnswers.addAnotherTechContact"))
        ),
        cyaHelper.ipRightsSection(false).value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided,
          companyApplying.name)(messages).toString

      application.stop()
    }

    "include a secondary technical contact section if data exists for it" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val userAnswers =
        UserAnswers(afaId)
          .set(WhoIsTechnicalContactPage, TechnicalContact("company name", "contact name", "telephone", "email")).success.value
          .set(IsTechnicalContactUkBasedPage, true).success.value
          .set(TechnicalContactUkAddressPage, UkAddress("line1", None, "town", None, "postcode")).success.value
          .set(TechnicalContactInternationalAddressPage, InternationalAddress("line1", None, "town", "country", None)).success.value
          .set(WhoIsSecondaryTechnicalContactPage, TechnicalContact("company name2", "contact name2", "telephone2", "email2")).success.value
          .set(IsSecondaryTechnicalContactUkBasedPage, true).success.value
          .set(SecondaryTechnicalContactUkAddressPage, UkAddress("line1 s", None, "town s", None, "postcode s")).success.value

          .set(CompanyApplyingPage, companyApplying).success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        AnswerSection(
          Some("checkYourAnswers.technicalContact"),
          Seq(
            cyaHelper.whoIsTechnicalContactName.value,
            cyaHelper.whoIsTechnicalContactCompany.value,
            cyaHelper.whoIsTechnicalContactPhone.value,
            cyaHelper.whoIsTechnicalContactEmail.value,
            cyaHelper.isTechnicalContactUkBased.value,
            cyaHelper.technicalContactUkAddress.value,
            cyaHelper.technicalContactInternationalAddress.value
          )
        ),
        AnswerSection(
          Some("checkYourAnswers.secondaryTechnicalContact"),
          Seq(
            cyaHelper.whoIsSecondaryTechnicalContactName.value,
            cyaHelper.whoIsSecondaryTechnicalContactCompany.value,
            cyaHelper.whoIsSecondaryTechnicalContactPhone.value,
            cyaHelper.whoIsSecondaryTechnicalContactEmail.value,
            cyaHelper.isSecondaryTechnicalContactUkBased.value,
            cyaHelper.secondaryTechnicalContactUkAddress.value
          ),
          Some(SectionLink(routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, "technical").url, "checkYourAnswers.removeSecondaryTechnicalContact"))
        ),
        cyaHelper.ipRightsSection(false).value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided,
          companyApplying.name)(messages).toString

      application.stop()
    }

    "include an IP Rights section if IP Rights data exists" in {

      val afaService = mock[AfaService]

      when(afaService.canCreateAfa(any())) thenReturn true

      val userAnswers =
        UserAnswers(afaId)
          .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value
          .set(CompanyApplyingPage, companyApplying).success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedSections = Seq(
        cyaHelper.ipRightsSection(true).value
      )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[AfaService].toInstance(afaService))
          .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(
          afaId,
          expectedSections,
          routes.SubmissionResultController.onPageLoad(afaId).url,
          "checkYourAnswers.publish",
          canCreateAfa = true,
          noEvidenceProvided,
          companyApplying.name
        )(messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "have a Continue call to action if the user has not entered enough data to form a valid AFA" in {

      val afaService = mock[AfaService]
      val navigator = mock[Navigator]
      val continue = Call("GET", "foo")

      when(afaService.canCreateAfa(any())) thenReturn false
      when(navigator.continue(any())) thenReturn continue

      val cyaHelper = new CheckYourAnswersHelper(baseAnswers)

      val expectedSections = Seq(
        cyaHelper.ipRightsSection(false).value
      )

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService),
            bind[Navigator].toInstance(navigator)
          )
          .build()

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(afaId, expectedSections, continue.url, "checkYourAnswers.continue", canCreateAfa = false, noEvidenceProvided,
          companyApplying.name)(messages).toString

      application.stop()
    }

    "have a Publish call to action and guidance if the user has entered enough data to form a valid AFA" in {

      val afaService = mock[AfaService]

      when(afaService.canCreateAfa(any())) thenReturn true

      val cyaHelper = new CheckYourAnswersHelper(baseAnswers)

      val expectedSections = Seq(
        cyaHelper.ipRightsSection(true).value
      )

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[AfaService].toInstance(afaService)
          )
          .build()

      val view = application.injector.instanceOf[CheckYourAnswersView]

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(afaId).url)

      val result = route(application, request).value

      val noEvidenceProvided = false

      contentAsString(result) mustEqual
        view(
          afaId,
          expectedSections,
          routes.SubmissionResultController.onPageLoad(afaId).url,
          "checkYourAnswers.publish",
          canCreateAfa = true,
          noEvidenceProvided,
          companyApplying.name
        )(messages).toString

      application.stop()
    }
  }
}
