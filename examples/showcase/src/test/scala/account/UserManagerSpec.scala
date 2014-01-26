package account

import org.scalatest.GivenWhenThen
import org.scalatest.FeatureSpec
import org.scalatest.Matchers
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser
import org.openqa.selenium.firefox.FirefoxDriver
import org.scalatest.BeforeAndAfter
import org.scalatest.time.Span
import org.scalatest.time.Seconds
import org.scalatest.BeforeAndAfterAll

class UserManagerSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterAll with WebBrowser {

  implicit val webDriver: WebDriver = new FirefoxDriver
  val host = "http://localhost:8080/showcase"

  feature("User Managament") {
    info("As a administrator")
    info("i want to list and edit the current users, but i don't want to add new user")

    scenario("List users") {
      Given("Go to user management page")
      goToUserManagementPage()
      Given("Login as admin if necessary")
      loginAsAdminIfNecessary()

      When("All users are listed in the page")

      Then("admin user is displayed in the page")
      val adminTd = find(xpath("//tr[1]/td[2]"))
      adminTd.get.text should be("管理员 ")
    }

    scenario("Edit user1") {
      Given("Go to user management page")
      goToUserManagementPage()
      Given("Login as admin if necessary")
      loginAsAdminIfNecessary()

      When("Edit user1 with new name")
      click on id("editLink-user")
      textField("name").value = "user_foo"
      click on id("submit_btn")

      Then("user1 with new name display in user edit page")
      click on id("editLink-user")
      textField("name").value should be("user_foo")
    }

    scenario("Edit admin") {
      Given("Go to user management page")
      Given("Login as admin if necessary")
      When("Edit admin with new name")
      Then("admin with new name display in user edit page")
      pending
    }
  }

  override def beforeAll() {
    implicitlyWait(Span(10, Seconds))
  }

  override def afterAll() {
    webDriver.close()
  }

  def goToUserManagementPage() {
    go to (host)
    pageTitle should include("Home")
    click on linkText("帐号管理")
  }

  def loginAsAdminIfNecessary() {
    if (pageTitle contains "登录页") {
      textField("username").value = "admin"
      pwdField("password").value = "admin"
      checkbox("rememberMe").select()
      click on id("submit_btn")

      pageTitle should include("综合演示用例")
    }
  }
}