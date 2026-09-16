package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MockWebTest {

    //Invoke browser ->Invoke tab/page->type url
    Page page;
    Browser browser;
    Playwright playwright;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(5000);//apply for all the locator actions
        page.navigate("https://eventhub.rahulshettyacademy.com/login");

    }
    @Test(description = "Sandbox banner is shown when 6 events are returned")
    public void DemoTest() {
        page.getByPlaceholder("you@email.com").fill("vasu.bellavi@gmail.com");
        page.getByLabel("Password").fill("Asalipura@123");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        //Step 1- validate 6 events
        page.route("**/api/events**", route -> route.fulfill(
                new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events6.json"))));
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        //page.waitForTimeout(7000);

        Locator eventCards = page.getByTestId("event-card");
        assertThat(eventCards.first()).isVisible();//5 seconds

        Assert.assertEquals(eventCards.count(), 6);
        assertThat(page.locator(".mx-1").first()).isVisible();

        //validate 4 cards
        page.route("**/api/events**", route -> route.fulfill(
                new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events4.json"))));

        page.navigate("https://eventhub.rahulshettyacademy.com/events");


        Locator eventCards1 = page.getByTestId("event-card");
        assertThat(eventCards1.first()).isVisible();//5 seconds

        Assert.assertEquals(eventCards1.count(), 4);
        assertThat(page.locator(".mx-1").first()).isHidden();
    }
     @Test
     public void routeResumeTest()
        {
            //route.resume()
            page.getByPlaceholder("you@email.com").fill("vasu.bellavi@gmail.com");
            page.getByLabel("Password").fill("Asalipura@123");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
            assertThat(page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

            //bookings
            page.getByTestId("nav-bookings").click();

            page.route("**/api/bookings**", route -> route.resume(new Route.ResumeOptions().
                    setUrl("https://api.eventhub.rahulshettyacademy.com/api/bookings/72116")));
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().
                    setName("View Details")).first().click();
            assertThat(page.getByText("Access Denied")).isVisible();

        }
    }

