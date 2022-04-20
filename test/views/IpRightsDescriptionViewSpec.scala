/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IpRightsDescriptionFormProvider
import models.{IpRightsType, NormalMode, UserAnswers}
import pages.IpRightsTypePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.IpRightsDescriptionView

class IpRightsDescriptionViewSpec extends TextInputViewBehaviours[String] {

  val messageKeyPrefix = "ipRightsDescription"

  val form = new IpRightsDescriptionFormProvider()()

  val rightsType = IpRightsType.Design

  "IpRightsDescriptionView view" must {

    val userAnswers = UserAnswers(afaId).set(IpRightsTypePage(0), rightsType).success.value

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val view = application.injector.instanceOf[IpRightsDescriptionView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, afaId, Some(rightsType))(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(rightsType.toString.toLowerCase),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextArea(
      form,
      applyView,
      messageKeyPrefix,
      routes.IpRightsDescriptionController.onSubmit(NormalMode, 0, afaId).url,
      Seq("value"),
      argsUsedInBrowserTitle = true,
      Seq(rightsType.toString.toLowerCase)
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
