package account

import org.scalatest.GivenWhenThen
import org.scalatest.FeatureSpec
import org.scalatest.Matchers

class userManagerSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Manage users") {
    info("As a administrator")
    info("i want to list and edit the current users, but i don't want to add new user")

    scenario("List users") {
      Given("login as administrator")
      When("login success and redirect to default page")
      val a = 1
      Then("admin user is displayed in page")
      a should be > 0
    }

    scenario("Edit user1") {
      Given("login as administrator")
      When("edit user1 with new name")
      val a = 1
      Then("user1 with new name display in page")
      a should be > 2
    }

    scenario("Edit admin") {
      Given("login as administrator")
      When("edit admin and update his name")
      Then("admin had update the name")
      pending
    }
  }
}