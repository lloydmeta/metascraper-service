package models

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSpec}
import play.api.test.Helpers._
import com.beachape.metascraper.Messages.ScrapedData
import scala.Some
import play.api.test.FakeApplication
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class MetadataScraperSpec extends FunSpec with ShouldMatchers with BeforeAndAfter {

  val url = "http://beachape.com"
  val scraper = MetadataScraper("http://beachape.com")
  val scrapedData = ScrapedData(
    url = "abc.com",
    title = "the title",
    description = "the description",
    mainImageUrl = "mainImageUrl",
    imageUrls = Seq("one", "two")
  )

  describe("#scrapedDataToJson") {

    it("should respond with a JsValue that is essentially a serialised version of the ScrapedData object") {
      val jsonValue = scraper.scrapedDataToJson(scrapedData)

      val Some(url) = (jsonValue \ "url").asOpt[String]
      url should be(scrapedData.url)

      val Some(title) = (jsonValue \ "title").asOpt[String]
      title should be(scrapedData.title)

      val Some(description) = (jsonValue \ "description").asOpt[String]
      description should be(scrapedData.description)

      val Some(mainImageUrl) = (jsonValue \ "mainImageUrl").asOpt[String]
      mainImageUrl should be(scrapedData.mainImageUrl)

      val Some(imageUrls) = (jsonValue \ "imageUrls").asOpt[Seq[String]]
      imageUrls should be(scrapedData.imageUrls)
    }
  }

  describe("#failedScrapeToJson") {

    val failed = new Throwable("lol")

    it("should respond with a JsValue that is essentially a serialised version of the FailedToScrapeUrl object") {
      val jsonValue = scraper.failedScrapeToJson(failed)
      val Some(message) = (jsonValue \ "error").asOpt[String]
      message should be(failed.getMessage)
    }
  }

  // Requires a running application
  describe("#getCachedJsValueForUrl and #cacheJsValueForUrl") {

    it("should return None if passed a url that was never cached") {
      running(FakeApplication()) {
        scraper.removeCachedValueForUrl()
        scraper.getCachedJsValueForUrl().map(_ should be(None))
      }
    }

    it("should return a previously cached JsValue for a url that is currently cached") {
      running(FakeApplication()) {
        scraper.removeCachedValueForUrl()
        scraper.cacheJsValueForUrl(scraper.scrapedDataToJson(scrapedData))
        scraper.getCachedJsValueForUrl().map(_ should be(Some(scraper.scrapedDataToJson(scrapedData))))
      }
    }
  }

  describe("#removeCachedValueForUrl()") {

    it("should cause #getCachedJsValueForUrl to return none even if previously cached") {
      running(FakeApplication()) {
        scraper.cacheJsValueForUrl(scraper.scrapedDataToJson(scrapedData))
        scraper.removeCachedValueForUrl()
        scraper.getCachedJsValueForUrl().map(_ should be(None))
      }
    }

  }

}