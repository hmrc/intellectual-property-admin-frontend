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

import org.jsoup.nodes.Document
import views.behaviours.ViewBehaviours
import views.html.ConfirmedRemovedContactView

class ConfirmedRemovedContactViewSpec extends ViewBehaviours {

  "ConfirmedRemovedContactView" must {
    "when passed technical return the confirmed removed technical contact page" must {
      val view = injectInstanceOf[ConfirmedRemovedContactView](Some(emptyUserAnswers))

      val applyView = view.apply(afaId, "technical")(messages)

      behave like normalPageUsingDesignSystem(
        frontendAppConfig,
        applyView,
        "confirmedRemovedContact",
        args = Seq("Technical", afaId),
        argsUsedInBrowserTitle = true,
        afaIdInHeader = false
      )

      behave like pageWithButtonLinkUsingDesignSystem(
        applyView,
        "confirmedRemovedContact.returnToCheck",
        controllers.routes.CheckYourAnswersController.onPageLoad(afaId).url
      )

      "Confirmation panel" must {
        implicit val doc: Document = asDocument(applyView)

        "display the correct confirmation heading" in {
          doc.getElementsByClass("govuk-panel").size() mustBe 1
          element("#main-content > div > div > div > h1").text() mustBe
            s"Technical contact removed from application $afaId"
        }
      }
    }

    "when passed legal return the confirmed removed legal contact page" must {
      val view = injectInstanceOf[ConfirmedRemovedContactView](Some(emptyUserAnswers))

      val applyView = view.apply(afaId, "legal")(messages)

      behave like normalPageUsingDesignSystem(
        frontendAppConfig,
        applyView,
        "confirmedRemovedContact",
        args = Seq("Legal", afaId),
        argsUsedInBrowserTitle = true,
        afaIdInHeader = false
      )

      behave like pageWithButtonLinkUsingDesignSystem(
        applyView,
        "confirmedRemovedContact.returnToCheck",
        controllers.routes.CheckYourAnswersController.onPageLoad(afaId).url
      )

      "Confirmation panel" must {
        implicit val doc: Document = asDocument(applyView)

        "display the correct confirmation heading" in {
          doc.getElementsByClass("govuk-panel").size() mustBe 1
          element("#main-content > div > div > div > h1").text() mustBe
            s"Legal contact removed from application $afaId"
        }
      }
    }
  }
}
