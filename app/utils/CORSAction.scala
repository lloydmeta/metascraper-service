package utils

import play.api.mvc._

/**
 * Methods for making an action return with CORS headers
 *
 * Stolen from https://github.com/opensas/BackboneBootstrap/blob/master/webservice/app/utils/CORSAction.scala
 *
 */
object CORSAction {

  type ResultWithHeaders = Result { def withHeaders(headers: (String, String)*): Result }

  def apply(block: Request[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
    Action {
      request =>
        block(request).withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS")
    }
  }

  def apply(block: => ResultWithHeaders): Action[AnyContent] = {
    this.apply(_ => block)
  }

}