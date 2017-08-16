package controllers.basic

import jp.t2v.lab.play2.auth.sample.Account
import jp.t2v.lab.play2.auth.{AsyncIdContainer, AuthenticityToken}
import play.api.mvc.RequestHeader

import scala.concurrent.{ExecutionContext, Future}

class BasicAuthIdContainer extends AsyncIdContainer[Account] {
  override def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext): Future[Unit] = {
    Future.successful(())
  }

  override def get(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Option[Account]] = Future {
    val Pattern = "(.*?):(.*)".r
    PartialFunction.condOpt(token) {
      case Pattern(user, pass) => Account.authenticate(user, pass)
    }.flatten
  }

  override def remove(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Unit] = {
    Future.successful(())
  }

  override def startNewSession(userId: Account, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext): Future[AuthenticityToken] = {
    throw new AssertionError("don't use")
  }
}
