/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package utils

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{NiceClassId, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.IpRightsNiceClassPage
import viewmodels.ReviewRow

class ReviewHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val iprIndex = 0

  "ReviewHelper" should {

    "return a list of Review Rows for an IPR based on the Nice Class items" in {

      forAll(arbitrary[UserAnswers], Gen.listOfN(2, arbitrary[NiceClassId]).map(_.zipWithIndex)) {
        (userAnswers, niceClasses) =>

          val updatedUserAnswers: UserAnswers =
            niceClasses.foldLeft(userAnswers) {
              case (userAnswers, (niceClassName, index)) =>
                userAnswers.set(IpRightsNiceClassPage(iprIndex, index), niceClassName).success.value
          }

          val expectedReviewRows: Seq[ReviewRow] = niceClasses.map {
            case (niceClass, niceClassIndex) => ReviewRow(
              niceClass.toString,
              Some(routes.DeleteNiceClassController.onPageLoad(NormalMode, userAnswers.id, iprIndex, niceClassIndex)),
              routes.IpRightsNiceClassController.onPageLoad(NormalMode, iprIndex, niceClassIndex, userAnswers.id)
            )
          }

          val reviewHelper = new ReviewHelper(updatedUserAnswers)

          val result = reviewHelper.niceClassReviewRow(NormalMode, iprIndex).value

          result mustBe expectedReviewRows
       }
    }

    "return a single Review Row for an IPR based on the Nice Class items" in {

      forAll(arbitrary[UserAnswers], Gen.listOfN(1, arbitrary[NiceClassId]).map(_.zipWithIndex)) {
        (userAnswers, niceClasses) =>

          val updatedUserAnswers: UserAnswers =
            niceClasses.foldLeft(userAnswers) {
              case (userAnswers, (niceClassName, index)) =>
                userAnswers.set(IpRightsNiceClassPage(iprIndex, index), niceClassName).success.value
            }

          val expectedReviewRows: Seq[ReviewRow] = niceClasses.map {
            case (niceClass, niceClassIndex) => ReviewRow(
              niceClass.toString,
              None,
              routes.IpRightsNiceClassController.onPageLoad(NormalMode, iprIndex, niceClassIndex, userAnswers.id)
            )
          }

          val reviewHelper = new ReviewHelper(updatedUserAnswers)

          val result = reviewHelper.niceClassReviewRow(NormalMode, iprIndex).value

          result mustBe expectedReviewRows
      }
    }
  }
}
