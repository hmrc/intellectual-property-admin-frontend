/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

sealed trait ContactOptions

object ContactOptions extends Enumerable.Implicits {

  case object RepresentativeContact extends WithName("representativeContact") with ContactOptions
  case object LegalContact extends WithName("legalContact") with ContactOptions
  case object SecondaryLegalContact extends WithName("secondaryLegalContact") with ContactOptions
  case object SomeoneElse extends WithName("someoneElse") with ContactOptions

  val values: Seq[ContactOptions] = Seq(
    RepresentativeContact, LegalContact, SecondaryLegalContact, SomeoneElse
  )
  implicit val enumerable: Enumerable[ContactOptions] =
    Enumerable(values.map(value => value.toString -> value): _*)
}
