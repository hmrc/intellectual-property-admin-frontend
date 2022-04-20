/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.PermissionToDestroySmallConsignmentsFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.PermissionToDestroySmallConsignmentsView

class PermissionToDestroySmallConsignmentsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "permissionToDestroySmallConsignments"

  val form = new PermissionToDestroySmallConsignmentsFormProvider()()

  "PermissionToDestroySmallConsignments view" must {

    val view = injectInstanceOf[PermissionToDestroySmallConsignmentsView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.PermissionToDestroySmallConsignmentsController.onSubmit(NormalMode, afaId).url
    )

    behave like pageWithGuidance(
      applyView(form),
      "permissionToDestroySmallConsignments.paragraph",
      Seq("reason", "applicability")
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
