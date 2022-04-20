/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.DeleteNiceClassFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.DeleteNiceClassView

class DeleteNiceClassViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "deleteNiceClass"

  val form = new DeleteNiceClassFormProvider()()

  "DeleteNiceClass view" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId))).build()

    val view = application.injector.instanceOf[DeleteNiceClassView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, 1, 1)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.DeleteNiceClassController.onSubmit(NormalMode, afaId, 1, 1).url
    )

    behave like pageWithSubmitButton(applyView(form))
  }
}
