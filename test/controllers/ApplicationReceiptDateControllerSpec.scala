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

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import forms.ApplicationReceiptDateFormProvider
import models.auditing.DraftStarted
import models.{AfaId, NormalMode, Region, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.ApplicationReceiptDatePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import queries.PublicationDeadlineQuery
import services.{AfaService, WorkingDaysService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import views.html.ApplicationReceiptDateView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class ApplicationReceiptDateControllerSpec
    extends SpecBase
    with MockitoSugar
    with LockAfaChecks
    with BeforeAndAfterEach {

  def onwardRoute: Call = Call("GET", "/foo")

  val afaId: AfaId = userAnswersId

  val formProvider = new ApplicationReceiptDateFormProvider()

  private def form = formProvider()

  lazy val applicationReceiptDateRoute: String =
    routes.ApplicationReceiptDateController.onPageLoad(NormalMode, afaId).url

  override val emptyUserAnswers: UserAnswers = UserAnswers(afaId)

  val validAnswer: LocalDate = LocalDate.now(ZoneOffset.UTC)

  val auditConnector: AuditConnector = mock[AuditConnector]

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, applicationReceiptDateRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, applicationReceiptDateRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  override def beforeEach(): Unit =
    Mockito.reset(auditConnector)

  "ApplicationReceiptDate Controller" must {

    "return OK and the correct view for a GET" in {
      val mockAfaService: AfaService = mock[AfaService]
      val application                = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AfaService].toInstance(mockAfaService))
        .build()

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)
      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[ApplicationReceiptDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ApplicationReceiptDatePage, validAnswer).success.value

      val mockAfaService: AfaService = mock[AfaService]

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[AfaService].toInstance(mockAfaService))
        .build()

      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

      val view = application.injector.instanceOf[ApplicationReceiptDateView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val mockAfaService: AfaService = mock[AfaService]
      val mockWorkingDaysService     = mock[WorkingDaysService]
      val arbitraryFutureDate        = LocalDate.now.plusDays(30)
      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)
      when(
        mockWorkingDaysService.workingDays(eqTo(Region.EnglandAndWales), any(), any())(any(), any())
      ) thenReturn Future.successful(arbitraryFutureDate)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[AfaService].toInstance(mockAfaService),
            bind[WorkingDaysService].toInstance(mockWorkingDaysService),
            bind[AuditConnector].toInstance(auditConnector)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      verify(auditConnector, times(1)).sendExplicitAudit[DraftStarted](
        eqTo("startedApplicationForAction"),
        eqTo(
          DraftStarted(
            id = afaId,
            PID = "id",
            userName = "name"
          )
        )
      )(any(), any(), any())

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, applicationReceiptDateRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ApplicationReceiptDateView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, afaId)(fakeRequest, messages).toString

      application.stop()
    }

    "Return Ok to with the ApplicationReceiptDateView for a GET if no existing data is found" in {

      val mockAfaService: AfaService = mock[AfaService]

      val mockWorkingDaysService = mock[WorkingDaysService]
      val arbitraryFutureDate    = LocalDate.now.plusDays(30)
      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)
      when(
        mockWorkingDaysService.workingDays(eqTo(Region.EnglandAndWales), any(), any())(any(), any())
      ) thenReturn Future.successful(arbitraryFutureDate)

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[AfaService].toInstance(mockAfaService),
          bind[WorkingDaysService].toInstance(mockWorkingDaysService)
        )
        .build()

      val result = route(application, getRequest()).value

      status(result) mustEqual OK

      val view = application.injector.instanceOf[ApplicationReceiptDateView]

      contentAsString(result) mustEqual
        view(form, NormalMode, afaId)(getRequest(), messages).toString

      application.stop()
    }

    "set data into the session repository when valid data is submitted and there are existing user answers" in {

      val mockAfaService: AfaService = mock[AfaService]

      val mockWorkingDaysService = mock[WorkingDaysService]
      val arbitraryFutureDate    = LocalDate.now.plusDays(30)
      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)
      when(
        mockWorkingDaysService.workingDays(eqTo(Region.EnglandAndWales), any(), any())(any(), any())
      ) thenReturn Future.successful(arbitraryFutureDate)

      val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId)))
        .overrides(
          bind[AfaService].toInstance(mockAfaService),
          bind[WorkingDaysService].toInstance(mockWorkingDaysService),
          bind[AuditConnector].toInstance(auditConnector)
        )
        .build()

      val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
      route(application, postRequest()).value.futureValue

      verify(mockAfaService).set(captor.capture())(any())

      val expected = UserAnswers(afaId)
        .set(ApplicationReceiptDatePage, validAnswer)
        .success
        .value
        .set(PublicationDeadlineQuery, arbitraryFutureDate)
        .success
        .value

      val value = captor.getValue
      value.id mustEqual expected.id
      value.data mustEqual expected.data

      verify(auditConnector, times(1)).sendExplicitAudit[DraftStarted](
        eqTo("startedApplicationForAction"),
        eqTo(
          DraftStarted(
            id = afaId,
            PID = "id",
            userName = "name"
          )
        )
      )(any(), any(), any())

      application.stop()
    }

    "set data into the session repository when valid data is submitted and there are no existing user answers" in {

      val mockAfaService: AfaService = mock[AfaService]

      val mockWorkingDaysService = mock[WorkingDaysService]
      val arbitraryFutureDate    = LocalDate.now.plusDays(30)
      when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)
      when(
        mockWorkingDaysService.workingDays(eqTo(Region.EnglandAndWales), any(), any())(any(), any())
      ) thenReturn Future.successful(arbitraryFutureDate)

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[AfaService].toInstance(mockAfaService),
          bind[WorkingDaysService].toInstance(mockWorkingDaysService),
          bind[AuditConnector].toInstance(auditConnector)
        )
        .build()

      val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
      route(application, postRequest()).value.futureValue

      verify(mockAfaService).set(captor.capture())(any())

      val expected = UserAnswers(afaId)
        .set(ApplicationReceiptDatePage, validAnswer)
        .success
        .value
        .set(PublicationDeadlineQuery, arbitraryFutureDate)
        .success
        .value

      val value = captor.getValue
      value.id mustEqual expected.id
      value.data mustEqual expected.data

      verify(auditConnector, times(1)).sendExplicitAudit[DraftStarted](
        eqTo("startedApplicationForAction"),
        eqTo(
          DraftStarted(
            id = afaId,
            PID = "id",
            userName = "name"
          )
        )
      )(any(), any(), any())

      application.stop()
    }

    "for a GET" must {

      redirectIfLocked(afaId, getRequest)
    }

    "for a POST" must {

      redirectIfLocked(afaId, postRequest)
    }

  }
}
