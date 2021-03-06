package jp.t2v.lab.play2.auth

import play.api.libs.crypto.CookieSigner
import play.api.mvc.{RequestHeader, Result}

trait TokenAccessor {

  def extract(signer: CookieSigner, request: RequestHeader): Option[AuthenticityToken]

  def put(signer: CookieSigner, token: AuthenticityToken)(result: Result)(implicit request: RequestHeader): Result

  def delete(result: Result)(implicit request: RequestHeader): Result

  protected def verifyHmac(signer: CookieSigner, token: SignedToken): Option[AuthenticityToken] = {
    val (hmac, value) = token.splitAt(40)
    if (safeEquals(signer.sign(value), hmac)) Some(value) else None
  }

  protected def sign(signer: CookieSigner, token: AuthenticityToken): SignedToken = signer.sign(token) + token

  // Do not change this unless you understand the security issues behind timing attacks.
  // This method intentionally runs in constant time if the two strings have the same length.
  // If it didn't, it would be vulnerable to a timing attack.
  protected def safeEquals(a: String, b: String): Boolean = {
    if (a.length != b.length) {
      false
    } else {
      var equal = 0
      for (i <- Array.range(0, a.length)) {
        equal |= a(i) ^ b(i)
      }
      equal == 0
    }
  }

}
