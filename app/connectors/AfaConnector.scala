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

package connectors

import models.afa.{InitialAfa, PublishedAfa}
import models.{AfaId, LockedException, Service, UserAnswers}
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AfaConnector @Inject() (
  config: Configuration,
  httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private val baseUrl = config.get[Service]("microservice.services.intellectual-property")

  private val afaId = s"$baseUrl/intellectual-property/afaId"

  def url(afaId: String) = s"$baseUrl/intellectual-property/afa/$afaId"

  def getNextAfaId()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[AfaId] =
    httpClient.GET[AfaId](afaId)

  def submit(afa: InitialAfa)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[PublishedAfa] =
    httpClient.PUT[InitialAfa, PublishedAfa](url(afa.id.toString), afa)

  def submitTestOnlyAfa(afa: String, afaId: String)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    httpClient.PUTString[HttpResponse](
      url(afaId),
      afa,
      Seq {
        "Content-Type" -> "application/json"
      }
    )

  def bulkInsert(afas: Seq[PublishedAfa])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Int] = {

    val url = s"$baseUrl/intellectual-property/afa/test-only/bulk-upsert"

    httpClient.POST[Seq[PublishedAfa], Int](url, afas)
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
    val url = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient.GET[Option[JsObject]](url)(lockable, implicitly, implicitly)
  }

  def set(afaId: AfaId, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient.PUT[UserAnswers, Boolean](url, userAnswers)(implicitly, lockable, implicitly, implicitly)
  }

  def removeDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    val url = s"$baseUrl/intellectual-property/draft-afa/$afaId"
    httpClient.DELETE[Option[UserAnswers]](url)(lockable, implicitly, implicitly)
  }

  def draftList(implicit hc: HeaderCarrier): Future[List[UserAnswers]] = {
    val url = s"$baseUrl/intellectual-property/draft-afas/list"
    httpClient.GET[List[UserAnswers]](url)(lockable, implicitly, implicitly)
  }
}
