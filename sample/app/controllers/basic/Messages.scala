package controllers.basic

import javax.inject.Inject
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.auth.sample.Role._
import play.api.Environment
import play.api.libs.crypto.CookieSigner
import play.api.mvc.{AbstractController, ControllerComponents}
import play.twirl.api.Html
import views.html

class Messages @Inject()(components: ControllerComponents, val environment: Environment, val signer: CookieSigner) extends AbstractController(components) with AuthElement
  with AuthConfigImpl {

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

  protected implicit def template(implicit user: User): String => Html => Html = html.basic.fullTemplate(user)

}