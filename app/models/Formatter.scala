/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        case AfaId.UK        =>
          f"UK${id.year}${id.id}%04d"
        case AfaId.GB        =>
          f"GB${id.year}${id.id}%03d"
        case AfaId.GB(false) =>
          f"GB${id.year}${id.id}%02d"
        case _               => throw new IllegalArgumentException(s"Unrecognised afaid id prefix $id")
      }
  }
}
