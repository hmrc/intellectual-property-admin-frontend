/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsTechnicalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsTechnicalContactUkBasedView

class IsTechnicalContactUkBasedPageViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isTechnicalContactUkBased"

  val infringementContactName = "foo"

  val form = new IsTechnicalContactUkBasedFormProvider()(infringementContactName)

  "IsTechnicalContactUkBased view" must {

    val view = injectInstanceOf[IsTechnicalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, infringementContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(infringementContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form,
      applyView,
      messageKeyPrefix,
      routes.IsTechnicalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(infringementContactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
