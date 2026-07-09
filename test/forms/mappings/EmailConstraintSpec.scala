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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.validation.{Invalid, Valid}

import scala.util.Random

class EmailConstraintSpec extends AnyWordSpec with Matchers with EmailValidation {

  "Email constraint" should {

    "return Valid" when {

      "email is shorter than 256 characters and follows correct pattern" in {

        val correctEmail = "name@example.com"

        validateEmail.apply(correctEmail) shouldBe Valid
      }

      "email has 256 characters and follow correct pattern" in {

        val correctEmail = Random.alphanumeric.take(244).mkString("") + "@example.com"

        correctEmail.length               shouldBe 256
        validateEmail.apply(correctEmail) shouldBe Valid
      }
    }

    "return Invalid" when {

      "email is longer than 256 characters" in {

        val incorrectEmail = Random.alphanumeric.take(245).mkString("") + "@example.com"

        incorrectEmail.length               shouldBe 257
        validateEmail.apply(incorrectEmail) shouldBe Invalid("email.length")
      }

      "email contains two @" in {

        val incorrectEmail = "name@example@.com"

        validateEmail.apply(incorrectEmail) shouldBe Invalid("email.format")
      }

      "email doesn't contain @" in {

        val incorrectEmail  = "nameexample.com"
        val incorrectEmail2 = "incorrectEmail"

        validateEmail.apply(incorrectEmail)  shouldBe Invalid("email.format")
        validateEmail.apply(incorrectEmail2) shouldBe Invalid("email.format")
      }

      "email is empty" in {

        val emptyEmail = ""

        validateEmail.apply(emptyEmail) shouldBe Invalid("email.required")
      }
    }
  }
}
