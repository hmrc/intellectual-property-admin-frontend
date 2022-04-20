/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait IpRightsType

object IpRightsType extends Enumerable.Implicits {

  case object Trademark extends WithName("trademark") with IpRightsType
  case object Copyright extends WithName("copyright") with IpRightsType
  case object Design extends WithName("design") with IpRightsType
  case object Patent extends WithName("patent") with IpRightsType
  case object PlantVariety extends WithName("plantVariety") with IpRightsType
  case object GeographicalIndication extends WithName("geographicalIndication") with IpRightsType
  case object SupplementaryProtectionCertificate extends WithName("supplementaryProtectionCertificate") with IpRightsType
  case object SemiconductorTopography extends WithName("semiconductorTopography") with IpRightsType

  val values: Seq[IpRightsType] = Seq(
    Trademark,
    Copyright,
    Design,
    Patent,
    PlantVariety,
    GeographicalIndication,
    SupplementaryProtectionCertificate,
    SemiconductorTopography
  )

  def radioItems(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        content = Text(messages(s"ipRightsType.${value.toString}")),
        id = Some(s"ipRightsType.${value.toString}"),
        value = Some(value.toString),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[IpRightsType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
