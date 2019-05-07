package models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_ABSENT)
case class AlgoliaIndexing(override val objectID: Option[String] = None,
                           property: Option[String] = None)
    extends Serializable
    with ObjectID

trait ObjectID { def objectID: Option[String] = None }
