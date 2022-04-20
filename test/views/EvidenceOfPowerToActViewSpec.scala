/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.EvidenceOfPowerToActFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.EvidenceOfPowerToActView

class EvidenceOfPowerToActViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "representativeEvidenceOfPowerToAct"

  val rightsHolderContactName = "dave"

  val form = new EvidenceOfPowerToActFormProvider()()

  "EvidenceOfPowerToAct view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
    val view: EvidenceOfPowerToActView = application.injector.instanceOf[EvidenceOfPowerToActView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, rightsHolderContactName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix,
      Seq("guidance", "guidance.bulletOne", "guidance.bulletTwo", "guidance.check"))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.EvidenceOfPowerToActController.onSubmit(NormalMode, afaId).url,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
