/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views.designsystembasedcomponents

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.footer.FooterItem

class FooterLinks @Inject()(
                             appConfig: FrontendAppConfig
                           ) {

  def accessibilityLink(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("accessibilityStatement.footerUrl.text")),
    Some(appConfig.manageIprAccessibilityUrl)
  )

  def items(implicit messages: Messages) = Seq(
    accessibilityLink
  )
}
