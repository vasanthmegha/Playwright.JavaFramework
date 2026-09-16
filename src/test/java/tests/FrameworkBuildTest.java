package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildTest extends TestBase{

    @Test(groups = {"framework"}, description = "Create Event -Book that event and verify if its booked")
    public void DemoTest() {
        String eventTitle="Framework Playwright test";

        LoginPage loginPage = new LoginPage(page,base_url);
        DashboardPage dashboardPage=loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();
        AdminEventPage adminEventPage=new AdminEventPage(page);
        adminEventPage.goTo();

        //Step 1- Create Event from Admin page
        adminEventPage.createEvent(
                eventTitle,
                "Playwright test des",
                "Test city",
                "Test Venue",
                "2026-09-18T07:26",
                "100",
                "50");

        //Step 2 - Find newly created event in the events page
        EventsPage eventsPage=new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard=eventsPage.findEventCard(eventTitle);
        int seatsNumBeforeBooking=eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage=eventsPage.proceedToBookingEvent(targetCard);
        bookingFormPage.fillAndConfirm("Test Student",
                "test.student@example.com",
                "9986636637"
                );





    }

}
