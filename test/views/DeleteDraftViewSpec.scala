/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.DeleteDraftFormProvider
import models.UserAnswers
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.DeleteDraftView

class DeleteDraftViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "deleteDraft"

  val form = new DeleteDraftFormProvider()()

  val rightsHolder = "Rights Holder"

  "DeleteDraft view" must {

    val view = injectInstanceOf[DeleteDraftView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable = view.apply(form, afaId, rightsHolder)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, Seq(rightsHolder), argsUsedInBrowserTitle = true)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix,
      routes.DeleteDraftController.onSubmit(afaId).url, args = Seq(rightsHolder), argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
