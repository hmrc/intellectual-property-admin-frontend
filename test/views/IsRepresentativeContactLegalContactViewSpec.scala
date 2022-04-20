/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsRepresentativeContactLegalContactFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsRepresentativeContactLegalContactView

class IsRepresentativeContactLegalContactViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isRepresentativeContactLegalContact"

  val form = new IsRepresentativeContactLegalContactFormProvider()()

  "IsRepresentativeContactLegalContact view" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[IsRepresentativeContactLegalContactView]

    val representativecontactName = "representativeContactName"

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, representativecontactName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(representativecontactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form,
      applyView,
      messageKeyPrefix,
      routes.IsRepresentativeContactLegalContactController.onSubmit(NormalMode, afaId).url,
      Seq(representativecontactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))
  }
}