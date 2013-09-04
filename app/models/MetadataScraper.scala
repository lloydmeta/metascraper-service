package models

import play.libs.Akka
import akka.routing.SmallestMailboxRouter
import akka.pattern.ask

import com.beachape.metascraper.Messages._
import com.beachape.metascraper.ScraperActor
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Companion object to hold ScraperActor references
 */
object MetadataScraper {
  type Url = String
  lazy val metadataScraperActorsRoundRobin = Akka.system.actorOf(ScraperActor().withRouter(SmallestMailboxRouter(5)), "router")

  def apply(url: Url): MetadataScraper = {
    new MetadataScraper(url)
  }
}

/**
 * Object for scraping data from a Url that was passed in during construction
 *
 * Most of the heavy lifing is done by the ScraperActors in the companion object,
 * which in turn depends on the Metascraper library
 *
 * Should be constructed/instantiated by using the companion's apply method
 */
class MetadataScraper(val url: Url) {

  /**
   * Returns a future Json of scraping
   * @return Future Json
   */
  def scrape(): Future[JsValue] = {
    implicit val timeout = Timeout(30 seconds)
    val futureResult = ask(MetadataScraper.metadataScraperActorsRoundRobin, ScrapeUrl(url)).mapTo[Either[FailedToScrapeUrl, ScrapedData]]
    futureResult map {
      case Left(fail) => failedScrapeToJson(fail)
      case Right(data) => scrapedDataToJson(data)
    }
  }

  /**
   * Returns a Json object for a passed in ScrapedData message
   * @param data ScrapedData message object
   * @return JsValue
   */
  def scrapedDataToJson(data: ScrapedData): JsValue = {
    Json.obj(
      "title" -> data.title,
      "description" -> data.description,
      "url" -> data.url,
      "mainImageUrl" -> data.mainImageUrl,
      "imageUrls" -> Json.toJson(data.imageUrls)
    )
  }

  /**
   * Returns a Json object containing the failure message from a FailedToScrapeUrl
   * object
   * @param fail FailedToScrapeUrl message object
   * @return JsValue
   */
  def failedScrapeToJson(fail: FailedToScrapeUrl): JsValue = {
    Json.obj(
      "message" -> fail.message
    )
  }
}
