/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import forms.IpRightsRegistrationEndFormProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.DateHintHelper
import views.behaviours.DateViewBehaviours
import views.html.IpRightsRegistrationEndView
import java.time.LocalDate

class IpRightsRegistrationEndViewSpec extends DateViewBehaviours {

  val messageKeyPrefix = "ipRightsRegistrationEnd"

  val rightType = "design"

  val form = new IpRightsRegistrationEndFormProvider()(Seq(rightType))

  "IpRightsRegistrationEndView view" must {

    val view = injectInstanceOf[IpRightsRegistrationEndView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, 0, afaId, rightType)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(rightType),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like datePage(form, applyView, messageKeyPrefix, Seq(DateHintHelper.dateInFuture(LocalDate.now)))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
