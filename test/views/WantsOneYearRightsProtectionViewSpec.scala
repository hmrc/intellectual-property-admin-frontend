/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.WantsOneYearRightsProtectionFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.WantsOneYearRightsProtectionView

class WantsOneYearRightsProtectionViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "wantsOneYearRightsProtection"

  val form = new WantsOneYearRightsProtectionFormProvider()()

  "WantsOneYearRightsProtection view" must {

    val view = injectInstanceOf[WantsOneYearRightsProtectionView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.WantsOneYearRightsProtectionController.onSubmit(NormalMode, afaId).url
    )

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("paragraph1", "paragraph2"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
