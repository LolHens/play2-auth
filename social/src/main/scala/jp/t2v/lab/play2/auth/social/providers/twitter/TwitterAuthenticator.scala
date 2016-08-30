package jp.t2v.lab.play2.auth.social.providers.twitter

import javax.inject.{Inject, Singleton}

import jp.t2v.lab.play2.auth.social.core.OAuth10aAuthenticator
import play.api.Configuration
import play.api.libs.oauth.ConsumerKey

@Singleton
class TwitterAuthenticator @Inject()(config: Configuration) extends OAuth10aAuthenticator {

  type AccessToken = TwitterOAuth10aAccessToken

  val providerName: String = "twitter"

  val requestTokenURL = "https://api.twitter.com/oauth/request_token"

  val accessTokenURL = "https://api.twitter.com/oauth/access_token"

  val authorizationURL = "https://api.twitter.com/oauth/authorize"

  lazy val consumerKey = ConsumerKey(
    config.get[String]("twitter.consumerKey"),
    config.get[String]("twitter.consumerSecret")
  )

  lazy val callbackURL = config.get[String]("twitter.callbackURL")

}

