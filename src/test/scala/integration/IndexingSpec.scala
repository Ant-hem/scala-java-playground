package integration

import com.algolia.search.SearchIndex
import models.AlgoliaIndexing

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.language.postfixOps

class IndexingSpec extends IntegrationSpec {

  val index: SearchIndex[AlgoliaIndexing] =
    searchClient.initIndex(getTestIndexName("indexing"), classOf[AlgoliaIndexing])

  after(index delete)

  test("Indexing test") {
    // AddObject with ID
    val objectOne = AlgoliaIndexing(Some("one"))
    val addObjectOneFuture = index.saveObjectAsync(objectOne).toScala

    val objectWoId = AlgoliaIndexing()
    val addObjectWoIdFuture = index.saveObjectAsync(objectWoId, true).toScala

    val objectsWithIds =
      Seq(AlgoliaIndexing(Some("two"), Some("test")), AlgoliaIndexing(Some("three")))
    val objectsWithIdsFuture = index.saveObjectsAsync(objectsWithIds.asJava).toScala

    val objectsWoIds = Seq(
      AlgoliaIndexing(property = Some("addObjectsWoId")),
      AlgoliaIndexing(property = Some("addObjectsWoId"))
    )
    val addObjectsWoIdsFuture = index.saveObjectsAsync(objectsWoIds.asJava, true).toScala

    val objectsToBatch =
      Seq.tabulate(1000)(n => AlgoliaIndexing(Some(n.toString), Some("Property" + n.toString)))

    val ids = objectsToBatch.flatten(x => x.objectID)

    val batchFuture = index.saveObjectsAsync(objectsToBatch.asJava).toScala

    val aggFut = for {
      f1 <- addObjectOneFuture.map(_.waitTask)
      f2 <- addObjectWoIdFuture.map(_.waitTask)
      f3 <- objectsWithIdsFuture.map(_.waitTask)
      f4 <- addObjectsWoIdsFuture.map(_.waitTask)
      f5 <- batchFuture.map(_.waitTask)
    } yield (f1, f2, f3, f4, f5)

    Await.ready(aggFut, Duration.Inf)

    val getIds = index.getObjectsAsync(ids.asJava).toScala

    whenReady(getIds) { x =>
      x should have size 1000
    }

  }
}
