/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import java.time.format.DateTimeFormatter

import base.SpecBase
import constants.PublishedAfaConstants.publishedAfa
import models.UserAnswers
import models.afa.PublishedAfa
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AfaService, LockService}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.SubmissionResult
import views.html.SubmissionResultView

import scala.concurrent.Future

class SubmissionResultControllerSpec extends SpecBase with MockitoSugar with ScalaCheckPropertyChecks {

  val submissionService: AfaService = mock[AfaService]
  val lockService: LockService = mock[LockService]

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  "SubmissionResult Controller" must {

    "submit the AFA and remove the draft and lock, returning OK and the correct view for a GET" in {

      val afa: PublishedAfa = publishedAfa

        reset(submissionService)

        val application =
          applicationBuilder(userAnswers = Some(UserAnswers(afa.id)))
            .overrides(
              bind[AfaService].toInstance(submissionService),
              bind[LockService].toInstance(lockService)
            )
            .build()

        when(submissionService.submit(any())(any())) thenReturn Future.successful(afa)

        when(submissionService.removeDraft(any())(any())) thenReturn Future.successful(Some(emptyUserAnswers))

        when(lockService.removeLock(any())(any())) thenReturn Future.successful(())

        val request = FakeRequest(GET, routes.SubmissionResultController.onPageLoad(afa.id).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SubmissionResultView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(SubmissionResult(afa.id, afa.applicant.name, afa.expirationDate.format(dateFormatter)))(fakeRequest, messages).toString

        verify(submissionService, times(1)).submit(any())(any())
        verify(submissionService, times(1)).removeDraft(eqTo(afa.id))(any())
        verify(lockService, times(1)).removeLock(eqTo(afa.id))(any())

        application.stop()

    }
  }
}
