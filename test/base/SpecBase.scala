/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package base

import config.FrontendAppConfig
import controllers.actions._
import generators.ModelGenerators
import models.{AfaId, UserAnswers}
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import org.scalacheck.Arbitrary.arbitrary
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with TryValues with ScalaFutures with IntegrationPatience with ModelGenerators {

  val userAnswersId: AfaId = arbitrary[AfaId].sample.value

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId, Json.obj())

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(
                                    userAnswers: Option[UserAnswers] = None,
                                    getLock: Boolean = true,
                                    afaLockedUrl: String = "/"
                                  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[AfaDraftDataRetrievalAction].toInstance(new FakeAfaDraftDataRetrievalActionProvider(userAnswers)),
        bind[LockAfaActionProvider].toInstance(new FakeLockAfaActionProvider(getLock, afaLockedUrl))
      )
}
