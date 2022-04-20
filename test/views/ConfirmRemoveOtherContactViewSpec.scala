/*
 * Copyright 2022 HM Revenue & Customs
 *
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
