/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import base.SpecBase
import models.{AfaId, UserAnswers}
import play.api.mvc.Request
import play.api.http.Writeable
import play.api.test.Helpers._

trait LockAfaChecks extends SpecBase {

  def redirectIfLocked[A](
    afaId: AfaId,
    request: () => Request[A],
    userAnswers: UserAnswers = emptyUserAnswers
  )(implicit writeable: Writeable[A]): Unit =
    s"redirect to Afa Locked when a lock cannot be acquired for this Afa" in {

      val application =
        applicationBuilder(
          userAnswers = Some(userAnswers),
          getLock = false,
          afaLockedUrl = routes.UnlockAfaController.onPageLoad(afaId).url
        ).build()

      val result = route(application, request()).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.UnlockAfaController.onPageLoad(afaId).url

      application.stop()
    }
}
