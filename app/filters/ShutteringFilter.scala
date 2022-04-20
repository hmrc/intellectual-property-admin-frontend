/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.{Call, Filter, RequestHeader, Result}
import views.html.ShutterPage
import play.api.mvc.Results.ServiceUnavailable

import scala.concurrent.Future

@Singleton
class ShutteringFilter @Inject() (
                                   configuration: Configuration,
                                   shutterPage: ShutterPage
                                 )(implicit val mat: Materializer) extends Filter {

  private val shuttered: Boolean = configuration
    .getOptional[Boolean]("shuttered")
    .getOrElse(false)

  private val excludedPaths: Seq[Call] = configuration
    .getOptional[String]("shutter.urls.excluded")
    .getOrElse("")
    .split(",").map {
    path =>
      Call("GET", path.trim)
  }

  private def toCall(rh: RequestHeader): Call =
    Call(rh.method, rh.uri)

  override def apply(next: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] =
    if (shuttered && !excludedPaths.contains(toCall(rh))) Future.successful(ServiceUnavailable(shutterPage())) else next(rh)
}
