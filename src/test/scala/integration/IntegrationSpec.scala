package integration

import java.time.{ZoneOffset, ZonedDateTime}

import com.algolia.search.{DefaultSearchClient, Defaults, SearchClient}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import wrapper.SearchClientWrapper

class IntegrationSpec
    extends FunSuite
    with BeforeAndAfterAll
    with BeforeAndAfter
    with Inspectors
    with Matchers
    with ScalaFutures {

  implicit val patience: PatienceConfig =
    PatienceConfig(timeout = Span(30000, Seconds), interval = Span(500, Millis))

  override protected def beforeAll(): Unit =
    Defaults.getObjectMapper.registerModule(DefaultScalaModule)

  lazy val applicationId: String = System.getenv("ALGOLIA_APPLICATION_ID")
  lazy val apiKey: String = System.getenv("ALGOLIA_ADMIN_API_KEY")
  lazy val userName: String = System.getProperty("user.name")
  lazy val osName: String = System.getProperty("os.name").trim
  lazy val javaVersion: String = System.getProperty("java.version")

  lazy val searchClient: SearchClient = DefaultSearchClient.create(applicationId, apiKey)
  lazy val searchClientWrapper: SearchClientWrapper = new SearchClientWrapper(applicationId, apiKey)

  def getTestIndexName(indexName: String): String = {
    val utc = ZonedDateTime.now(ZoneOffset.UTC)
    s"scala_jvm_${javaVersion}_${utc}_${osName}_${userName}_$indexName"
  }

}
