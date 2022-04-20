/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IpRightsRegistrationNumberFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.IpRightsRegistrationNumberView

class IpRightsRegistrationNumberViewSpec extends TextInputViewBehaviours[String] {

  val messageKeyPrefix = "ipRightsRegistrationNumber"

  val ipRightsTypeName = "registration"

  val form = new IpRightsRegistrationNumberFormProvider()(ipRightsTypeName, Seq("firstRegistrationNumber", "secondRegistrationNumber"))

  "IpRightsRegistrationNumberView view" must {

    val view = injectInstanceOf[IpRightsRegistrationNumberView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, afaId, ipRightsTypeName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(ipRightsTypeName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.IpRightsRegistrationNumberController.onSubmit(NormalMode, 0, afaId).url,
      Seq("value"),
      titleArgs = Seq(ipRightsTypeName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
