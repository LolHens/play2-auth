package controllers.rememberme

import javax.inject.Inject
import jp.t2v.lab.play2.auth.LoginLogout
import jp.t2v.lab.play2.auth.sample.Accounts
import play.api.Environment
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.crypto.CookieSigner
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{AbstractController, ControllerComponents}
import views.html

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(components: ControllerComponents, val environment: Environment, val accounts: Accounts, val signer: CookieSigner)(implicit val executionContext: ExecutionContext) extends AbstractController(components) with LoginLogout with AuthConfigImpl {

  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(accounts.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }
  val remembermeForm = Form {
    "rememberme" -> boolean
  }

  def login = Action { implicit request =>
    Ok(html.rememberme.login(loginForm, remembermeForm.fill(request.session.get("rememberme").exists("true" ==))))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ))
  }

  def authenticate = Action.async { implicit request =>
    val rememberme = remembermeForm.bindFromRequest()
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.rememberme.login(formWithErrors, rememberme))), { user =>
        val req = request.addAttr(TypedKey("rememberme"), rememberme.get.toString)
        gotoLoginSucceeded(user.get.id)(req, executionContext).map(_.withSession("rememberme" -> rememberme.get.toString))
      }
    )
  }

}