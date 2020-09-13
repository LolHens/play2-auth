package controllers.stateless

import javax.inject.Inject
import controllers.stack.Pjax
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.auth.sample.Accounts
import jp.t2v.lab.play2.auth.sample.Role._
import play.api.Environment
import play.api.libs.crypto.CookieSigner
import play.api.mvc.{AbstractController, ControllerComponents, InjectedController}
import views.html

class Messages @Inject()(components: ControllerComponents, val environment: Environment, val accounts: Accounts, val signer: CookieSigner) extends AbstractController(components) with Pjax with AuthElement with AuthConfigImpl {

  def main = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "message main"
    Ok(html.message.main(title))
  }

  def list = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "all messages"
    Ok(html.message.list(title))
  }

  def detail(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "messages detail "
    Ok(html.message.detail(title + id))
  }

  def write = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val title = "write message"
    Ok(html.message.write(title))
  }

  protected val fullTemplate: User => Template = html.stateless.fullTemplate.apply

}