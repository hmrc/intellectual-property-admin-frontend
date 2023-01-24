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

package controllers

import base.SpecBase
import models.{AfaId, TechnicalContact, UkAddress, UserAnswers, WhoIsSecondaryLegalContact}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AfaService

import java.time.LocalDateTime
import scala.concurrent.Future

class RemoveOtherContactControllerSpec extends SpecBase with LockAfaChecks with MockitoSugar {

  val afaId: AfaId                           = userAnswersId
  val localTime: LocalDateTime               = LocalDateTime.now()
  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId, lastUpdated = localTime)

  val addAnotherLegalContact                                   = true
  val secondaryLegalContactDetails: WhoIsSecondaryLegalContact =
    WhoIsSecondaryLegalContact("name", "companyName", "phone", "email")
  val secondaryLegalContactUkBased                             = true
  val secondaryLegalContactAddress: UkAddress                  = UkAddress("street", None, "town", None, "postcode")

  val addAnotherTechContact                         = true
  val secondaryTechContactDetails: TechnicalContact = TechnicalContact("name", "companyName", "phone", "email")
  val secondaryTechContactUkBased                   = true
  val secondaryTechContactAddress: UkAddress        = UkAddress("street", None, "town", None, "postcode")

  def getRequestLegal: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, RemoveSecondaryLegalContactRoute)

  def getRequestTechnical: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, RemoveSecondaryTechnicalContactRoute)

  lazy private val RemoveSecondaryLegalContactRoute     = routes.RemoveOtherContactController.onDelete(afaId, "legal").url
  lazy private val RemoveSecondaryTechnicalContactRoute =
    routes.RemoveOtherContactController.onDelete(afaId, "technical").url

  "Company Applying UK Address Controller" must {

    "remove the secondary legal contact and load the correct view for a GET" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .set(AddAnotherLegalContactPage, addAnotherLegalContact)
        .success
        .value
        .set(WhoIsSecondaryLegalContactPage, secondaryLegalContactDetails)
        .success
        .value
        .set(IsApplicantSecondaryLegalContactUkBasedPage, secondaryLegalContactUkBased)
        .success
        .value
        .set(ApplicantSecondaryLegalContactUkAddressPage, secondaryLegalContactAddress)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val result = route(application, getRequestLegal).value

      status(result) mustEqual SEE_OTHER

      verify(mockAfaService, times(1)).set(
        eqTo(emptyUserAnswers.set(AddAnotherLegalContactPage, value = false).success.value)
      )(any())
    }

    "remove the secondary technical contact and load the correct view for a GET" in {

      val mockAfaService = mock[AfaService]

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .set(AddAnotherTechnicalContactPage, addAnotherLegalContact)
        .success
        .value
        .set(WhoIsSecondaryTechnicalContactPage, secondaryTechContactDetails)
        .success
        .value
        .set(IsSecondaryTechnicalContactUkBasedPage, secondaryTechContactUkBased)
        .success
        .value
        .set(SecondaryTechnicalContactUkAddressPage, secondaryTechContactAddress)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[AfaService].toInstance(mockAfaService)
          )
          .build()

      val result = route(application, getRequestTechnical).value

      status(result) mustEqual SEE_OTHER

      verify(mockAfaService, times(1)).set(
        eqTo(emptyUserAnswers.set(AddAnotherTechnicalContactPage, value = false).success.value)
      )(any())
    }
  }

  "redirect to Session Expired for a GET if no existing data is found" in {

    val application = applicationBuilder(userAnswers = None).build()

    val result = route(application, getRequestLegal).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

    application.stop()
  }

  "for a GET" must {
    redirectIfLocked(afaId, () => getRequestLegal)
  }
}
