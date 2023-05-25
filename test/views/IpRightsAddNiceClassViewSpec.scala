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

package views

import controllers.routes
import models.{NormalMode, UserAnswers}
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import viewmodels.ReviewRow
import views.behaviours.ViewBehaviours
import views.html.IpRightsAddNiceClassView

class IpRightsAddNiceClassViewSpec extends ViewBehaviours {

  val request = FakeRequest()

  val messageKeyPrefix      = "ipRightsAddNiceClass"
  val reviewRow1: ReviewRow =
    ReviewRow("1", None, routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, afaId))
  val reviewRow2: ReviewRow = ReviewRow(
    "2",
    Some(routes.DeleteNiceClassController.onPageLoad(NormalMode, afaId, 2, 2)),
    routes.IpRightsNiceClassController.onPageLoad(NormalMode, 3, 3, afaId)
  )

  "IpRightsAddNiceClass view" must {

    val view = injectInstanceOf[IpRightsAddNiceClassView](Some(UserAnswers(afaId)))

    def applyView(): HtmlFormat.Appendable =
      view.apply(
        NormalMode,
        0,
        afaId,
        Seq.empty,
        routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, afaId).url
      )(messages, request)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(), messageKeyPrefix)

    behave like pageWithGuidance(applyView(), messageKeyPrefix, Seq("hint"))

    behave like pageWithBackLink(applyView())

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView())

    implicit val doc = asDocument(
      view.apply(
        NormalMode,
        0,
        afaId,
        Seq(reviewRow1, reviewRow2),
        routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, afaId).url
      )(messages, request)
    )

    "render a row without a delete option that" must {
      "have the correct key value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe reviewRow1.name
      }
      "have a link saying change that takes you to the change nice class page" in {
        val changeButton = element("#main-content > div > div > dl > div:nth-child(1) > dd > a")
        changeButton.attr("href") mustBe reviewRow1.changeUrl.toString
        changeButton.text() mustBe s"Change Nice class ${reviewRow1.name}"
      }
      "have no delete button" in {
        elementText("#main-content > div > div > dl > div:nth-child(1)") mustNot include("Delete")
      }
    }

    "render a row with a delete option that" must {
      "have the correct key value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe reviewRow2.name
      }
      "have 2 links" in {
        element("#main-content > div > div > dl > div:nth-child(2)")
          .getElementsByClass("govuk-summary-list__actions")
          .size() mustBe 2
      }
      "have a link saying change that takes you to the change nice class page" in {
        val changeButton = element("#main-content > div > div > dl > div:nth-child(2) > dd:nth-child(3) > a")
        changeButton.attr("href") mustBe reviewRow2.changeUrl.url
        changeButton.text() mustBe s"Change Nice class ${reviewRow2.name}"
      }
      "have a link saying delete that takes you to the delete nice class page" in {
        val changeButton = element("#main-content > div > div > dl > div:nth-child(2) > dd:nth-child(2) > a")
        changeButton.attr("href") mustBe reviewRow2.deleteUrl.get.url
        changeButton.text() mustBe s"Delete Nice class ${reviewRow2.name}"
      }
    }

    "render a link to add more nice classes that" must {
      "have the correct link text" in {
        element("#main-content > div > div > a.govuk-link").text() mustBe "Add another Nice class"
      }
      "link to the correct page" in {
        element("#main-content > div > div > a.govuk-link")
          .attr("href") mustBe routes.CheckIprDetailsController.onPageLoad(NormalMode, 0, afaId).url
      }
    }
  }
}
