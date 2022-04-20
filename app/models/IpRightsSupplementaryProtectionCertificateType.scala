/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait IpRightsSupplementaryProtectionCertificateType

object IpRightsSupplementaryProtectionCertificateType extends Enumerable.Implicits {

  case object Medicinal extends WithName("medicinal") with IpRightsSupplementaryProtectionCertificateType
  case object PlantProtection extends WithName("plantProtection") with IpRightsSupplementaryProtectionCertificateType

  val values: Seq[IpRightsSupplementaryProtectionCertificateType] = Seq(
    Medicinal, PlantProtection
  )

  def radioItems(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        content = Text(messages(s"ipRightsSupplementaryProtectionCertificateType.${value.toString}")),
        id = Some(s"ipRightsSupplementaryProtectionCertificateType.${value.toString}"),
        value = Some(value.toString),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[IpRightsSupplementaryProtectionCertificateType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
