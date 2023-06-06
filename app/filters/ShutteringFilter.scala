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
)(implicit val mat: Materializer)
    extends Filter {

  private val shuttered: Boolean = configuration
    .getOptional[Boolean]("shuttered")
    .getOrElse(false)

  private val excludedPaths: collection.Seq[Call] = configuration
    .getOptional[String]("shutter.urls.excluded")
    .getOrElse("")
    .split(",")
    .map { path =>
      Call("GET", path.trim)
    }

  private def toCall(rh: RequestHeader): Call =
    Call(rh.method, rh.uri)

  override def apply(next: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] =
    if (shuttered && !excludedPaths.contains(toCall(rh))) Future.successful(ServiceUnavailable(shutterPage()(rh)))
    else next(rh)
}
