/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package config

import com.google.inject.AbstractModule
import controllers.actions._
import services.{AfaService, DefaultAfaService, DefaultLockService, LockService}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[IdentifierAction]).to(classOf[StrideIdentifierAction]).asEagerSingleton()
    bind(classOf[ApiIdentifierAction]).to(classOf[StrideApiIdentifierAction])
    bind(classOf[AfaDraftDataRetrievalAction]).to(classOf[AfaDraftDataRetrievalActionProviderImpl]).asEagerSingleton()
    bind(classOf[LockAfaActionProvider]).to(classOf[LockAfaActionProviderImpl]).asEagerSingleton()
    bind(classOf[AfaService]).to(classOf[DefaultAfaService])
    bind(classOf[LockService]).to(classOf[DefaultLockService])
  }
}
