/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

trait Formatter[A] {
  def format(toFormat: A): String
}

object Formatter {

  implicit class HtmlFormattedOps[A: Formatter](a: A) {
    def format: String = implicitly[Formatter[A]].format(a)
  }

  implicit val afaIdEv: Formatter[AfaId] = new Formatter[AfaId] {
    override def format(id: AfaId): String =
      id.prefix match {
        case AfaId.UK =>
          f"UK${id.year}${id.id}%04d"
        case AfaId.GB =>
          f"GB${id.year}${id.id}%03d"
        case AfaId.GB(false) =>
          f"GB${id.year}${id.id}%02d"
        case _ => throw new IllegalArgumentException(s"Unrecognised afaid id prefix $id")
      }
  }
}
