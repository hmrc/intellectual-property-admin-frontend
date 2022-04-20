/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import forms.IpRightsNiceClassFormProvider
import models.{NiceClassId, NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import controllers.routes
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.IpRightsNiceClassView

class IpRightsNiceClassViewSpec extends TextInputViewBehaviours[NiceClassId] {

  val messageKeyPrefix = "ipRightsNiceClass"

  val form = new IpRightsNiceClassFormProvider()(Seq.empty)

  "IpRightsNiceClassView view" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[IpRightsNiceClassView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, 0, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.IpRightsNiceClassController.onPageLoad(NormalMode, 0, 0, afaId).url,
      Seq("value")
    )

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

  }
}
