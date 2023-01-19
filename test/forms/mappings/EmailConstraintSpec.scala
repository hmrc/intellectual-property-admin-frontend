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

package forms.mappings

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.validation.{Invalid, Valid}

import scala.util.Random

class EmailConstraintSpec extends AnyWordSpec with Matchers with EmailValidation {

  "Email constraint" should {

    "return Valid" when {

      "email is shorter than 256 characters and follows correct pattern" in {

        val correctEmail = "name@example.com"

        validateEmail.apply(correctEmail) mustEqual Valid
      }

      "email has 256 characters and follow correct pattern" in {

        val correctEmail = Random.alphanumeric.take(244).mkString("") + "@example.com"

        correctEmail.length mustBe 256
        validateEmail.apply(correctEmail) mustEqual Valid
      }
    }

    "return Invalid" when {

      "email is longer than 256 characters" in {

        val incorrectEmail = Random.alphanumeric.take(245).mkString("") + "@example.com"

        incorrectEmail.length mustBe 257
        validateEmail.apply(incorrectEmail) mustEqual Invalid("email.length")
      }

      "email contains two @" in {

        val incorrectEmail = "name@example@.com"

        validateEmail.apply(incorrectEmail) mustEqual Invalid("email.format")
      }

      "email doesn't contain @" in {

        val incorrectEmail  = "nameexample.com"
        val incorrectEmail2 = "incorrectEmail"

        validateEmail.apply(incorrectEmail) mustEqual Invalid("email.format")
        validateEmail.apply(incorrectEmail2) mustEqual Invalid("email.format")
      }

      "email is empty" in {

        val emptyEmail = ""

        validateEmail.apply(emptyEmail) mustEqual Invalid("email.required")
      }
    }
  }
}
