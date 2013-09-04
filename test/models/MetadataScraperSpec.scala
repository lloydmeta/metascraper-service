package models

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSpec}
import com.beachape.metascraper.Messages.{FailedToScrapeUrl, ScrapedData}

class MetadataScraperSpec extends FunSpec with ShouldMatchers with BeforeAndAfter {

  val scraper = MetadataScraper("http://beachape.com")

  describe("#scrapedDataToJson") {

    val scrapedData = ScrapedData(
      url = "abc.com",
      title = "the title",
      description = "the description",
      mainImageUrl = "mainImageUrl",
      imageUrls = Seq("one", "two")
    )

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

    val failed = FailedToScrapeUrl(message = "oops")

    it("should respond with a JsValue that is essentially a serialised version of the FailedToScrapeUrl object") {
      val jsonValue = scraper.failedScrapeToJson(failed)
      val Some(message) = (jsonValue \ "message").asOpt[String]
      message should be(failed.message)
    }
  }

}