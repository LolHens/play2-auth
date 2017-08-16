package jp.t2v.lab.play2.auth.social.providers.slack

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}

import jp.t2v.lab.play2.auth.social.core.{AccessTokenRetrievalFailedException, OAuth2Authenticator}
import play.api.{Configuration, Logger}
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.ws._
import play.api.mvc.Results

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class SlackAuthenticator @Inject()(val ws: WSClient, config: Configuration) extends OAuth2Authenticator {

  type AccessToken = String

  override val providerName: String = "slack"

  override val authorizationUrl: String = "https://slack.com/oauth/authorize"

  override val accessTokenUrl: String = "https://slack.com/api/oauth.access"

  override val clientId: String = config.get[String]("slack.clientId")

  override val clientSecret: String = config.get[String]("slack.clientSecret")

  override val callbackUrl: String = config.get[String]("slack.callbackURL")

  def getAuthorizationUrl(scope: String, state: String): String = {
    val encodedClientId = URLEncoder.encode(clientId, "utf-8")
    val encodedRedirectUri = URLEncoder.encode(callbackUrl, "utf-8")
    val encodedScope = URLEncoder.encode(scope, "utf-8")
    val encodedState = URLEncoder.encode(state, "utf-8")
    s"${authorizationUrl}?client_id=${encodedClientId}&redirect_uri=${encodedRedirectUri}&scope=${encodedScope}&state=${encodedState}"
  }

  override def retrieveAccessToken(code: String)(implicit ctx: ExecutionContext): Future[AccessToken] = {
    ws.url(accessTokenUrl)
      .withQueryStringParameters(
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "redirect_uri" -> callbackUrl,
        "code" -> code)
      .withHttpHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .post(EmptyBody)
      .map { response =>
        Logger(getClass).debug("Retrieving access token from provider API: " + response.body)
        parseAccessTokenResponse(response)
      }
  }

  override def parseAccessTokenResponse(response: WSResponse): AccessToken = {
    val j = response.json
    try {
      (j \ "access_token").as[String]
    } catch {
      case NonFatal(e) =>
        throw new AccessTokenRetrievalFailedException("Failed to retrieve access token", e)
    }
  }

}
