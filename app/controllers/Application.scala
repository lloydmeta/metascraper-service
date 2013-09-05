package controllers

import models.MetadataScraper
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import utils.CORSAction

object Application extends Controller {

  // Form for parsing POSTed body
  val scrapeForm = Form(
    mapping(
      "url" -> text
    )(Tuple1.apply)(Tuple1.unapply)
  )

  def index = Action {
    Ok(views.html.index("Hello ! You're probably looking to POST to /scrape :)"))
  }

  /**
   * Endpoint that asynchronously does the scraping
   */
  def scrape = CORSAction { implicit request =>
    try {
      val formValues = scrapeForm.bindFromRequest.get
      Async {
        val scraper = MetadataScraper(formValues._1)
        scraper.scrape().map(Ok(_))
      }
    } catch {
      case e: Exception => Ok(Json.obj("message" -> "Params error"))
    }
  }

  /**
   * Action for allowing CORs
   *
   * Stolen from https://github.com/opensas/BackboneBootstrap/blob/master/webservice/app/controllers/Application.scala
   */
  def options(url: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
      // cache access control response for one day
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }
}