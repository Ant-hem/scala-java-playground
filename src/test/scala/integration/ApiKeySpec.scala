package integration

import com.algolia.search.models.apikeys.ApiKey

import scala.collection.JavaConverters._

class ApiKeySpec extends IntegrationSpec {

  var addedApiKey: Option[String] = None

  test("ApiKey test") {

    val apiKeyToSend = new ApiKey()
      .setDescription("A description")
      .setMaxHitsPerQuery(1000L)
      .setMaxQueriesPerIPPerHour(1000)
      .setQueryParameters("typoTolerance=strict")
      .setValidity(600L)
      .setReferers(List("referer").asJava)
      .setIndexes(List("indexes").asJava)
      .setAcl(List("search").asJava)

    val addKeyFuture = searchClientWrapper.addApiKey(apiKeyToSend)

    whenReady(addKeyFuture) { x =>
      addedApiKey = Some(x.getKey)
      apiKeyToSend.setValue(x.getKey)
      x.waitTask()
    }

    val getKeyFuture = searchClientWrapper.getApiKey(addedApiKey.get)

    whenReady(getKeyFuture) { x =>
      x.getValue shouldEqual addedApiKey.get
    }

    val listApiKeysFuture = searchClientWrapper.listApiKeys()

    whenReady(listApiKeysFuture) { x =>
      x.map(_.getValue) should contain(addedApiKey.get)
    }

    apiKeyToSend.setMaxHitsPerQuery(42L)

    val updateApiKeyFuture = searchClientWrapper.updateApiKey(apiKeyToSend)

    whenReady(updateApiKeyFuture) { x =>
      x.waitTask()
    }

    val getUpdatedKey = searchClientWrapper.getApiKey(addedApiKey.get)

    whenReady(getUpdatedKey) { x =>
      x.getMaxHitsPerQuery shouldEqual 42
    }

    val deleteApiKey = searchClientWrapper.deleteApiKey(addedApiKey.get)

    whenReady(deleteApiKey) { x =>
      x.waitTask()
    }

  }

}
