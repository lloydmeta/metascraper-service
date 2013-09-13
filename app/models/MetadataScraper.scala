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
import play.api.cache.Cache
import play.api.Play.current

/**
 * Companion object to hold ScraperActor references
 */
object MetadataScraper {

  type Url = String
  val cachedUrlTTLinSeconds: Int = 600
  lazy val metadataScraperActorsRoundRobin = Akka.system.actorOf(ScraperActor().withRouter(SmallestMailboxRouter(20)), "router")

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
 * Essentially, if a value exists in cache return it
 * otherwise fetch it and cache the JSON value and return that
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
    // Excuse the flatMap and doubled wrapped futures
    getCachedJsValueForUrl() flatMap {
      // This is wrapped inside a future because further down, we are inside another future
      case Some(jsValue) => Future.successful(jsValue)
      case None => {
        val futureResult = ask(
          MetadataScraper.metadataScraperActorsRoundRobin,
          ScrapeUrl(url, userAgent = "GeosocialMetacraper")).mapTo[Either[Throwable, ScrapedData]]
        // We are inside a future of a future
        futureResult map {
          case Left(fail) => failedScrapeToJson(fail)
          case Right(data) => {
            val scrapedDataJson = scrapedDataToJson(data)
            Future { cacheJsValueForUrl(scrapedDataJson) }
            scrapedDataJson
          }
        }
      }
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
  def failedScrapeToJson(fail: Throwable): JsValue = {
    Json.obj(
      "message" -> fail.getMessage
    )
  }

  /**
   * Returns Future[Option[JsValue]] if the information for a URL exists in the
   * cache
   *
   * @return Future[Option[JsValue]]
   */
  def getCachedJsValueForUrl(): Future[Option[JsValue]] = Future { Cache.getAs[String](urlToCacheKey).map(Json.parse(_)) }

  /**
   * Throws a given JsValue into the cache
   * @param jsValue value to be cached
   */
  def cacheJsValueForUrl(jsValue: JsValue) {
    Cache.set(urlToCacheKey, Json.stringify(jsValue), MetadataScraper.cachedUrlTTLinSeconds)
  }

  /**
   * Removes the stored value in the cache for this MetadataScraper's URL
   */
  def removeCachedValueForUrl() {
    Cache.remove(urlToCacheKey)
  }

  /**
   * Returns a cache key for the current url
   * @return String Cache key
   */
  private def urlToCacheKey: String = s"urlCacheKey.${url}"
}
