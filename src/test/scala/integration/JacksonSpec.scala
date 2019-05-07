package integration

import com.algolia.search.Defaults
import models.Employee

class JacksonSpec extends IntegrationSpec {

  test("should serialize case class") {
    val employee = Employee("company", "name")

    val json = Defaults.getObjectMapper.writeValueAsString(employee)

    json should not be empty
  }

}
