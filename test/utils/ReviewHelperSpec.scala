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
