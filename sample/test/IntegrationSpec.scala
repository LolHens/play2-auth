package test

import org.specs2.mutable._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

class IntegrationSpec extends Specification {

  "Standard Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(baseURL)
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secretxxx")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.getCookie("PLAY2AUTH_SESS_ID").getExpiry must not beNull

      // logout
      browser.$("a").click()
      browser.pageSource must contain("Sign in")

      browser.goTo(s"$baseURL/standard/messages/write")
      browser.pageSource must contain("Sign in")

    }

    "authorize" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"

      // login succeded
      browser.goTo(baseURL)
      browser.$("#email").write("bob@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      browser.goTo(s"${baseURL}/standard/messages/write")
      browser.pageSource must contain("no permission")

      browser.goTo(s"${baseURL}/standard/logout")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.goTo(s"${baseURL}/standard/messages/write")
      browser.pageSource must not contain ("no permission")

    }

  }

  "Builder Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(s"${baseURL}/builder/")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secretxxx")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      // logout
      browser.$("a").click()
      browser.pageSource must contain("Sign in")

      browser.goTo(s"$baseURL/builder/messages/write")
      browser.pageSource must contain("Sign in")

    }

    "authorize" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"

      // login succeded
      browser.goTo(s"${baseURL}/builder/")
      browser.$("#email").write("bob@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      browser.goTo(s"${baseURL}/builder/messages/write")
      browser.pageSource must contain("no permission")

      browser.goTo(s"${baseURL}/builder/logout")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.goTo(s"${baseURL}/builder/messages/write")
      browser.pageSource must not contain ("no permission")

    }

  }

  "CSRF Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login
      browser.goTo(s"${baseURL}/csrf/")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      // submit with token form
      browser.$("#message").write("testmessage")
      browser.$("#submitbutton").click()
      browser.pageSource must contain("testmessage")

      // submit without token form
      browser.goTo(s"$baseURL/csrf/without_token")
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.$("#message").write("testmessage")
      browser.$("#submitbutton").click()
      browser.pageSource must not contain ("testmessage")

    }

  }

  "Ephemeral Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(s"${baseURL}/ephemeral/")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secretxxx")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.getCookie("PLAY2AUTH_SESS_ID").getExpiry must beNull

      // logout
      browser.$("a").click()
      browser.pageSource must contain("Sign in")

      browser.goTo(s"$baseURL/ephemeral/messages/write")
      browser.pageSource must contain("Sign in")

    }

    "authorize" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"

      // login succeded
      browser.goTo(s"${baseURL}/ephemeral/")
      browser.$("#email").write("bob@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.getCookie("PLAY2AUTH_SESS_ID").getExpiry must beNull

      browser.goTo(s"${baseURL}/ephemeral/messages/write")
      browser.pageSource must contain("no permission")

      browser.goTo(s"${baseURL}/ephemeral/logout")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.goTo(s"${baseURL}/ephemeral/messages/write")
      browser.pageSource must not contain ("no permission")

    }

  }

  "Stateless Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(s"$baseURL/stateless/")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secretxxx")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      // logout
      browser.$("a").click()
      browser.pageSource must contain("Sign in")

      browser.goTo(s"$baseURL/stateless/messages/write")
      browser.pageSource must contain("Sign in")

    }

    "authorize" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"

      // login succeded
      browser.goTo(s"$baseURL/stateless/")
      browser.$("#email").write("bob@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      browser.goTo(s"${baseURL}/stateless/messages/write")
      browser.pageSource must contain("no permission")

      browser.goTo(s"${baseURL}/stateless/logout")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.goTo(s"${baseURL}/stateless/messages/write")
      browser.pageSource must not contain ("no permission")

    }

  }

  "HTTP Basic Auth Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(s"$baseURL/basic/")
      browser.url must equalTo("/basic/messages/main")

    }

  }

  "Remember Me Sample" should {

    "work from within a browser" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"
      // login failed
      browser.goTo(s"$baseURL/rememberme/")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secretxxx")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.getCookie("PLAY2AUTH_SESS_ID").getExpiry must beNull

      // logout
      browser.$("a").click()
      browser.pageSource must contain("Sign in")

      browser.goTo(s"$baseURL/rememberme/messages/write")
      browser.pageSource must contain("Sign in")

      // login succeded
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#rememberme").click()
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")
      browser.getCookie("PLAY2AUTH_SESS_ID").getExpiry must not beNull

      browser.$("a").click()

    }

    "authorize" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = GuiceApplicationBuilder().configure(inMemoryDatabase(name = "default", options = Map("DB_CLOSE_DELAY" -> "-1"))).build()) {

      val baseURL = s"http://localhost:${port}"

      // login succeded
      browser.goTo(s"$baseURL/rememberme/")
      browser.$("#email").write("bob@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.pageSource must not contain ("Sign in")
      browser.pageSource must contain("logout")

      browser.goTo(s"${baseURL}/rememberme/messages/write")
      browser.pageSource must contain("no permission")

      browser.goTo(s"${baseURL}/rememberme/logout")
      browser.$("#email").write("alice@example.com")
      browser.$("#password").write("secret")
      browser.$("#loginbutton").click()
      browser.$("dl.error").size must equalTo(0)
      browser.goTo(s"${baseURL}/standard/messages/write")
      browser.pageSource must not contain ("no permission")

    }

  }

}

