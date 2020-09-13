package controllers.rememberme

import jp.t2v.lab.play2.auth._
import play.api.libs.crypto.CookieSigner
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{Cookie, RequestHeader, Result}

class RememberMeTokenAccessor(maxAge: Int) extends CookieTokenAccessor() {

  override def put(signer: CookieSigner, token: AuthenticityToken)(result: Result)(implicit request: RequestHeader):
  Result = {
    val remember = request.attrs.get(TypedKey[String]("rememberme")).contains("true") || request.session.get("rememberme").contains("true")
    val _maxAge = if (remember) Some(maxAge) else None
    val c = Cookie(cookieName, sign(signer, token), _maxAge, cookiePathOption, cookieDomainOption, cookieSecureOption,
      cookieHttpOnlyOption)
    result.withCookies(c)
  }
}
