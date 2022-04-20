/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import generators.Generators
import models.{AfaId, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.http.Writeable
import play.api.inject.bind
import play.api.libs.json.Writes
import play.api.mvc.Request
import play.api.test.Helpers._
import services.AfaService

import scala.concurrent.Future

trait IprIndexValidation extends SpecBase with ScalaCheckPropertyChecks with MockitoSugar with Generators {

  def validateOnIprIndex[A, B](
                                generator: Gen[A],
                                createPage: Int => QuestionPage[A],
                                requestForIndex: Int => Request[B]
                              )(implicit writes: Writes[A], writeable: Writeable[B]): Unit = {

    "return not found if a given index is out of bounds" in {

      val gen = for {
        answers <- Gen.listOf(generator).map(_.zipWithIndex)
        index <- Gen.oneOf(
          Gen.chooseNum(answers.size + 1, answers.size + 100),
          Gen.chooseNum(-100, -1)
        )
      } yield (answers, index)

      forAll(gen, arbitrary[AfaId]) {
        case ((answers, index), afaId) =>

          val userAnswers = answers.foldLeft(UserAnswers(afaId)) {
            case (userAnswers, (answer, index)) =>
              userAnswers.set(createPage(index), answer).success.value
          }

          val mockAfaService = mock[AfaService]

          when(mockAfaService.set(any())(any())) thenReturn Future.successful(true)

          val application =
            applicationBuilder(Some(userAnswers))
              .overrides(bind[AfaService].toInstance(mockAfaService))
              .build()

          val result = route(application, requestForIndex(index)).value

          status(result) mustEqual NOT_FOUND

          application.stop()
      }
    }
  }
}
