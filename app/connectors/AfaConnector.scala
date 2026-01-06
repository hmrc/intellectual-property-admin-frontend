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

package connectors

import models.afa.{InitialAfa, PublishedAfa}
import models.{AfaId, LockedException, Service, UserAnswers}
import org.checkerframework.checker.units.qual.A
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AfaConnector @Inject() (
  config: Configuration,
  httpClient: HttpClientV2
)(implicit ec: ExecutionContext) {

  private val baseUrl = config.get[Service]("microservice.services.intellectual-property")

  private val afaIdUrl = s"$baseUrl/intellectual-property/afaId"

  def url(afaId: String): String = s"$baseUrl/intellectual-property/afa/$afaId"

  def getNextAfaId()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[AfaId] =
    httpClient
      .get(url"$afaIdUrl")
      .execute[AfaId]

  def submit(afa: InitialAfa)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[PublishedAfa] =
    httpClient
      .put(url"${url(afa.id.toString)}")
      .withBody(Json.toJson(afa))
      .execute[PublishedAfa]

  def submitTestOnlyAfa(afa: String, afaId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient
      .put(url"${url(afaId)}")
      .setHeader("Content-Type" -> "application/json")
      .withBody(afa)
      .execute[HttpResponse]

  def bulkInsert(afas: Seq[PublishedAfa])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Int] = {
    val url = s"$baseUrl/intellectual-property/afa/test-only/bulk-upsert"
    httpClient
      .post(url"$url")
      .withBody(Json.toJson(afas))
      .execute[Int]
  }

  private def lockable[A](implicit rds: HttpReads[A]): HttpReads[A] =
    new HttpReads[A] {
      override def read(method: String, url: String, response: HttpResponse): A =
        if (response.status == Status.LOCKED) {
          throw response.json.as[LockedException]
        } else {
          rds.read(method, url, response)
        }
    }

  def getDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[JsObject]] = {
    val draftUrl = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient
      .get(url"$draftUrl")
      .execute[Option[JsObject]](lockable, ec)
  }

  def set(afaId: AfaId, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val draftUrl = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient
      .put(url"$draftUrl")
      .withBody(Json.toJson(userAnswers))
      .execute[Boolean](lockable, ec)
  }

  def removeDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    val draftUrl = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient
      .delete(url"$draftUrl")
      .execute[Option[UserAnswers]](lockable, ec)
  }

  def draftList(implicit hc: HeaderCarrier): Future[List[UserAnswers]] = {
    val listUrl = s"$baseUrl/intellectual-property/draft-afas/list"
    httpClient
      .get(url"$listUrl")
      .execute[List[UserAnswers]](lockable, ec)
  }
}
