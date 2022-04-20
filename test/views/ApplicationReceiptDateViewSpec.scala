/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import forms.ApplicationReceiptDateFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateViewBehaviours
import views.html.ApplicationReceiptDateView

class ApplicationReceiptDateViewSpec extends DateViewBehaviours {

  val messageKeyPrefix = "applicationReceiptDate"
  val title: String = "When did HMRC receive the application? - Protect intellectual property rights"
  val heading: String = "When did HMRC receive the application?"

  val form = new ApplicationReceiptDateFormProvider()()

  "ApplicationReceiptDateView view" must {

    val view = injectInstanceOf[ApplicationReceiptDateView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like datePage(form, applyView, messageKeyPrefix, Seq("7 7 2021"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
