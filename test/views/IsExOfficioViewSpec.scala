/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsExOfficioFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsExOfficioView

class IsExOfficioViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isExOfficio"

  val form = new IsExOfficioFormProvider()()

  "IsExOfficio view" must {

    val view = injectInstanceOf[IsExOfficioView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, afaIdInHeader = false)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix,
      routes.IsExOfficioController.onSubmit(NormalMode, afaId).url)

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("paragraph"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
