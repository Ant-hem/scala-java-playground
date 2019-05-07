package integration

import java.util.Collections

import com.algolia.search.SearchIndex
import com.algolia.search.models.indexing.{Query, SearchForFacetRequest}
import com.algolia.search.models.settings.IndexSettings
import models.Employee

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class SearchSpec extends IntegrationSpec {

  val index: SearchIndex[Employee] =
    searchClient.initIndex(getTestIndexName("search"), classOf[Employee])

  after(index delete)

  test("Search index test") {

    val employees =
      Seq[Employee](
        Employee("Algolia", "Julien Lemoine"),
        Employee("Algolia", "Julien Lemoine"),
        Employee("Amazon", "Jeff Bezos"),
        Employee("Apple", "Steve Jobs"),
        Employee("Apple", "Steve Wozniak"),
        Employee("Arista Networks", "Jayshree Ullal"),
        Employee("Google", "Lary Page"),
        Employee("Google", "Rob Pike"),
        Employee("Google", "Sergueï Brin"),
        Employee("Microsoft", "Bill Gates"),
        Employee("SpaceX", "Elon Musk"),
        Employee("Tesla", "Elon Musk"),
        Employee("Yahoo", "Marissa Mayer")
      )

    val saveObjectsFuture =
      index.saveObjectsAsync(employees.asJava, true).toScala

    val settings =
      new IndexSettings().setAttributesForFaceting(Seq("searchable(company)").asJava)

    Await.ready(saveObjectsFuture.map(_.waitTask), Duration.Inf)

    val setSettingsFuture = index.setSettingsAsync(settings).toScala

    Await.ready(setSettingsFuture.map(_.waitTask), Duration.Inf)

    val searchAlgoliaFuture = index.searchAsync(new Query("algolia")).toScala

    val searchElonFuture =
      index.searchAsync(new Query("elon").setClickAnalytics(true)).toScala

    val searchElonFuture1 = index
      .searchAsync(
        new Query("elon")
          .setFacets(Collections.singletonList("*"))
          .setFacetFilters(Collections.singletonList(Collections.singletonList("company:tesla"))))
      .toScala

    val searchElonFuture2 = index
      .searchAsync(
        new Query("elon")
          .setFacets(Collections.singletonList("*"))
          .setFilters("(company:tesla OR company:spacex)"))
      .toScala

    val searchFacetFuture = index
      .searchForFacetValuesAsync(
        new SearchForFacetRequest().setFacetName("company").setFacetQuery("a"))
      .toScala

    val aggFut = for {
      f1Result <- searchAlgoliaFuture
      f2Result <- searchElonFuture
      f3Result <- searchElonFuture1
      f4result <- searchElonFuture2
      f5result <- searchFacetFuture
    } yield (f1Result, f2Result, f3Result, f4result, f5result)

    aggFut.onComplete {
      case Success(x) =>
        x._1.getHits should have size 2
        x._2.getQueryID should not be null
        x._3.getHits should have size 1
        x._4.getHits should have size 2
      case Failure(e) => e.printStackTrace()
    }

  }
}
