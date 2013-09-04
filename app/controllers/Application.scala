package controllers

import models.MetadataScraper
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json

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
  def scrape = Action { implicit request =>
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
}