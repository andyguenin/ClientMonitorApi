package org.downtowndailybread.integration.base

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.downtowndailybread.bethsaida.json.JsonSupport
import org.downtowndailybread.bethsaida.model.parameters.{LoginParameters, UserParameters}
import org.downtowndailybread.bethsaida.providers.{DatabaseConnectionProvider, SettingsProvider}
import org.downtowndailybread.bethsaida.{ApiMain, Settings}
import org.scalatest.FlatSpec
import spray.json.JsString

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

trait BethsaidaSupport
  extends FlatSpec
    with ScalatestRouteTest
    with JsonSupport
    with SettingsProvider
    with DatabaseConnectionProvider {
  val settings = new Settings(ConfigFactory.load("integration_test"))

  val apiMain = new ApiMain(settings)

  val routes = apiMain.routes

  val apiBaseUrl = "/api/v1"

  implicit val r = apiMain.rejectionHandler
  implicit val d = apiMain.exceptionHandler

  protected val authTokenPromise = Promise[String]()

  lazy val authToken = Await.result(authTokenPromise.future, 1.second)

  lazy val userParams = UserParameters(
    "Andy Guenin",
    LoginParameters(
      "andy@guenin.com",
      "AndyGueninPassword"
    )
  )

  class EnhancedHttpRequest(val httpRequest: HttpRequest) {
    def authenticate(): HttpRequest = {
      httpRequest.withHeaders(
        new Authorization(
          OAuth2BearerToken(authToken)
        )
      )
    }
  }

  implicit def httpRequestToEnhanced(httpRequest: HttpRequest): EnhancedHttpRequest = {
    new EnhancedHttpRequest(httpRequest)
  }

  implicit def stringToJsString(s: String): JsString = JsString(s)
}