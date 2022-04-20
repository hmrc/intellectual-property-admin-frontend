/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.CompanyApplyingFormProvider
import models.{CompanyApplying, NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.CompanyApplyingView

class CompanyApplyingViewSpec extends TextInputViewBehaviours[CompanyApplying] {

  val messageKeyPrefix = "companyApplying"

  override val form = new CompanyApplyingFormProvider()()

  "CompanyApplyingView" must {

    val view = injectInstanceOf[CompanyApplyingView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)


    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.CompanyApplyingController.onSubmit(NormalMode, afaId).url,
      Seq("companyName", "companyAcronym")
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
