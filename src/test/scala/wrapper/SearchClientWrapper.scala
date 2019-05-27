package wrapper

import com.algolia.search.models.apikeys._
import com.algolia.search.{DefaultSearchClient, SearchClient}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// A wrapper on top of the Java's Search client
// The purpose is to have a better DX using the Java library from Scala code
class SearchClientWrapper(applicationId: String, apiKey: String) {

  if (applicationId == null || applicationId.isEmpty) {
    throw new IllegalArgumentException(s"'applicationId' is probably too short: '$applicationId'")
  }

  if (apiKey == null || apiKey.isEmpty) {
    throw new IllegalArgumentException(s"'apiKey' is probably too short: '$apiKey'")
  }

  private val searchClient: SearchClient = DefaultSearchClient.create(applicationId, apiKey)

  // Converting Java Future to Scala Future
  def getApiKey(apiKey: String): Future[ApiKey] = {
    searchClient.getApiKeyAsync(apiKey).toScala
  }

  // Converting Java Future to Scala Future and converting Java list to Scala Seq
  def listApiKeys(): Future[Seq[ApiKey]] = {
    searchClient.listApiKeysAsync().toScala.map(_.asScala.toSeq)
  }

  def addApiKey(apiKey: ApiKey): Future[AddApiKeyResponse] = {
    searchClient.addApiKeyAsync(apiKey).toScala
  }

  def updateApiKey(apiKey: ApiKey): Future[UpdateApiKeyResponse] = {
    searchClient.updateApiKeyAsync(apiKey).toScala
  }

  def deleteApiKey(apiKey: String): Future[DeleteApiKeyResponse] = {
    searchClient.deleteApiKeyAsync(apiKey).toScala
  }

  def restoreApiKey(apiKey: String): Future[RestoreApiKeyResponse] = {
    searchClient.restoreApiKeyAsync(apiKey).toScala
  }

}
