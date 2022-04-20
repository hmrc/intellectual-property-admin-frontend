/*
 * Copyright 2022 HM Revenue & Customs
 *
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
                         )(implicit writeable: Writeable[A]): Unit = {

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
}
