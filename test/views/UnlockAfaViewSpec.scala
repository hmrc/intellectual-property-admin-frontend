/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.UnlockAfaFormProvider
import models.{Lock, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.UnlockAfaView

class UnlockAfaViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "unlockAfa"

  val form = new UnlockAfaFormProvider()()

  val existingLock = Lock(afaId, "id", "name")

  val rightsHolder = "rights holder"

  "UnlockAfa view" must {

    val view = injectInstanceOf[UnlockAfaView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, afaId, existingLock.name, Some(rightsHolder))(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(rightsHolder),
      afaIdInHeader = false,
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.UnlockAfaController.onSubmit(afaId).url,
      args = Seq(rightsHolder),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithGuidanceWithParameter(applyView(form), "unlockAfa.guidance", existingLock.name)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
