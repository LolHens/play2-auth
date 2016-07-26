import com.google.inject.AbstractModule
import jp.t2v.lab.play2.auth.sample.{AccountFixtures, Accounts}
import scalikejdbc.{AutoSession, DBSession}

class SampleModule extends AbstractModule {
  override protected def configure() = {
    bind(classOf[DBSession]).toInstance(AutoSession)
    bind(classOf[Accounts]).to(classOf[AccountFixtures])
  }
}
