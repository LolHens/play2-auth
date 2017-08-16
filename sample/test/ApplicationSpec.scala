package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import controllers.standard.{AuthConfigImpl, Messages}
import jp.t2v.lab.play2.auth.test.Helpers._
import java.io.File

import jp.t2v.lab.play2.auth.sample.{AccountFixtures, Accounts}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment}
import play.api.libs.crypto.CookieSigner
import scalikejdbc.AutoSession

class ApplicationSpec extends Specification {

  lazy val _accounts = new AccountFixtures()(AutoSession)

  def config(implicit app: Application) = new AuthConfigImpl {
    override val environment: Environment = Environment.simple()
    override val accounts: Accounts = _accounts
    override val signer = app.injector.instanceOf[CookieSigner]

  }

  "Messages" should {
    "return list when user is authorized" in new WithApplication(GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {
      implicit val s = AutoSession
      val res = new Messages(Environment.simple(), _accounts, app.injector.instanceOf[CookieSigner]).list(FakeRequest()
        .withLoggedIn(config)
      (1))
      contentType(res) must beSome("text/html")
    }
  }

}
