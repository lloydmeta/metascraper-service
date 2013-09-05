package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Hello")
      }
    }

    "respond to OPTIONS on /scrape with the proper headers" in {
      running(FakeApplication()) {
        val requiredOptionsHeaders = Map(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
          "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
          "Access-Control-Max-Age" -> (60 * 60 * 24).toString)
        val options = route(FakeRequest(method = "OPTIONS", "/scrape"))
        val Some(result) = options
        requiredOptionsHeaders.foreach { pair => headers(result) must havePair(pair) }
      }
    }

    "respond to POST on /scrape with the proper headers" in  {
      running(FakeApplication()) {
        val requiredHeaders = Map(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS")
        val json = Json.obj(
          "url" -> "http://beachape.com"
        )
        val request = route(
          FakeRequest(
            POST,
            "/scrape",
            headers = FakeHeaders(
              Seq( "Content-type" -> Seq( "application/json"))
            ),
            body = json))
        val Some(result) = request
        status(result) must equalTo(OK)
        contentType(result) must beSome.which(_ == "application/json")
        requiredHeaders.foreach { pair => headers(result) must havePair(pair) }
      }
    }
  }
}