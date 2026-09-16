package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicsTest {
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

    @Test(description = "Create Event -Book that event and verify if its booked")
    public void DemoTest() {
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        //page.getByLabel("Email").fill("vasanthkumarmv5@gmail.com");
        page.getByPlaceholder("you@email.com").fill("vasu.bellavi@gmail.com");
        page.getByLabel("Password").fill("Asalipura@123");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        //Step 1- Create Event from Admin page
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");

        //10 seconds - default Actions -fill(), Click() etc

        //customized wait option for specific locator
        page.locator("#event-title-input").fill("QA Demo Kumar");
        page.locator("#admin-event-form textarea").fill("Kumar QA Meetups");
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Test City");
        page.getByLabel("Venue").fill("Test Venue");
        page.getByLabel("Event Date & Time").fill("2026-09-18T07:26");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("50");

        //customized wait option for specific locator
        page.locator("#add-event-btn").click();

        //Event created! 2-3
        //default time out 5 seconds
        assertThat(page.getByText("Event created!")).isVisible();
        //Step 2 - Find newly created event in the events page
        page.locator("#nav-events").click();
        Locator eventCards = page.getByTestId("event-card");//{loc1,loc2, loc3}
        assertThat(eventCards.first()).isVisible();
        System.out.println(eventCards.count());
        //Visibility of the card which we have created
        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHasText("QA Demo Kumar"));
        assertThat(targetCard).isVisible();
        String seatsText = targetCard.getByText("seats").innerText();
        System.out.println(seatsText);

        int seatsNumBeforeBooking = Integer.parseInt(seatsText.split(" ")[0]);
        targetCard.getByTestId("book-now-btn").click();

        //Book the ticket for event
        page.getByLabel("Full Name").fill("Test Student");
        page.locator("#customer-email").fill("test.student@example.com");
        page.getByPlaceholder("+91 98765 43210").fill("9986636637");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();

        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
        String bookingRef = page.locator(".booking-ref").innerText();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View My Bookings")).click();

        //Verify in Booking History

        Locator bookingCards = page.locator("#booking-id");
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        //assertThat(targetBookingCard).isVisible();

        //seat count reduction check

        page.locator("#nav-events").click();
        page.waitForTimeout(1000);
        Locator eventCardsAfterBooking = page.getByTestId("event-card");
        //assertThat(eventCards.first()).isVisible();
        //Visibility of the card which we have created
        Locator targetCardAfterBooking = eventCardsAfterBooking.filter(new
                Locator.FilterOptions().setHasText("QA Demo Kumar"));
        assertThat(targetCardAfterBooking).isVisible();
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        System.out.println(seatsTextAfterBooking);

        //Afterbookings < BeforeBooking
        //49 seats available
        int seatsNumAfterBooking = Integer.parseInt(seatsTextAfterBooking.split(" ")[0]);

        Assert.assertTrue(seatsNumBeforeBooking > seatsNumAfterBooking);


    }

    @AfterMethod

    public void tearDown() {

    }

}
