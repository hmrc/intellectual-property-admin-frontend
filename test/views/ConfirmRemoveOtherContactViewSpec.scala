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

package views

import controllers.routes
import models.UserAnswers
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.ConfirmRemoveOtherContactView

class ConfirmRemoveOtherContactViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmRemoveOtherContact"

  "ConfirmRemoveOtherContactView" must {

    "When removing a legal contact" should {

      val view = injectInstanceOf[ConfirmRemoveOtherContactView](Some(UserAnswers(afaId)))

      def applyView: HtmlFormat.Appendable =
        view.apply(afaId, "legal")(fakeRequest, messages)

      behave like normalPageUsingDesignSystem(
        frontendAppConfig,
        applyView,
        messageKeyPrefix,
        Seq("legal"),
        argsUsedInBrowserTitle = true
      )

      behave like pageWithBackLink(applyView)

      behave like pageWithGuidanceWithParameter(applyView, s"$messageKeyPrefix.guidance", messageParameter = "legal")

      behave like pageWithButtonLinkUsingDesignSystem(
        applyView,
        s"$messageKeyPrefix.button.confirm",
        routes.RemoveOtherContactController.onDelete(afaId, "legal").url
      )
    }
    "When removing a technical contact" should {

      val view = injectInstanceOf[ConfirmRemoveOtherContactView](Some(UserAnswers(afaId)))

      def applyView: HtmlFormat.Appendable =
        view.apply(afaId, "technical")(fakeRequest, messages)

      behave like normalPageUsingDesignSystem(
        frontendAppConfig,
        applyView,
        messageKeyPrefix,
        Seq("technical"),
        argsUsedInBrowserTitle = true
      )

      behave like pageWithBackLink(applyView)

      behave like pageWithGuidanceWithParameter(applyView, s"$messageKeyPrefix.guidance", messageParameter = "technical")

      behave like pageWithButtonLinkUsingDesignSystem(
        applyView,
        s"$messageKeyPrefix.button.confirm",
        routes.RemoveOtherContactController.onDelete(afaId, "technical").url
      )
    }
  }
}
