package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITest {
    @Test public void e2eApiTest()
    {
        //login
        HashMap<Object,Object> loginPayload=new HashMap<>();//key, value
        loginPayload.put("email","vasu.bellavi@gmail.com");
        loginPayload.put("password","Asalipura@123");

        Playwright playwright= Playwright.create();
        APIRequestContext apiRequest=playwright.request().newContext();
        APIResponse loginResponse=apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/auth/login",
                RequestOptions.create().setData(loginPayload));
        Assert.assertTrue(loginResponse.ok());
        System.out.println(loginResponse.text());

        String token=JsonPath.read(loginResponse.text(),"$.token");
        System.out.println("Login success "+token);

        //Create Event
        String eventTitle="Playwright API Testing";
        HashMap<Object,Object> createEventPayload=new HashMap<>();
        createEventPayload.put("title", eventTitle);
        createEventPayload.put("description", "api");
        createEventPayload.put("category", "Conference");
        createEventPayload.put("venue", "Main road");
        createEventPayload.put("city", "Chennai");
        createEventPayload.put("eventDate", "2026-09-14T02:43:00.000Z");
        createEventPayload.put("price", 100);
        createEventPayload.put("totalSeats", 500);
        APIResponse eventResponse=apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/events"
                , RequestOptions.create().setHeader("Authorization","Bearer "+token)
                        .setData(createEventPayload));
        Assert.assertTrue(eventResponse.ok(),"Create Event API should succeed");

        int eventId=JsonPath.read(eventResponse.text(), "$.data.id");
        System.out.println("Event created and its ID is "+eventId);

        //Get Event
        APIResponse retriveEvents=apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setQueryParam("page","1")
                        .setQueryParam("limit","12")
                        .setHeader("Authorization","Bearer "+token));
        Assert.assertTrue(retriveEvents.ok(),"Event Retrieval API should succeed");
        System.out.println("Event created and its ID is "+retriveEvents);

        List<Integer>allEventIds=JsonPath.read(retriveEvents.text(),"$.data[*].id");
        Assert.assertTrue(allEventIds.contains(eventId),"Created event should appear in events list");

        //Delete Event
        APIResponse deleteResponse=apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/"
                +eventId, RequestOptions.create().setHeader("Authorization","Bearer "+token));
        Assert.assertTrue(deleteResponse.ok());

        //Verify Deletion is success, ->GetEvents and confirm that event id does not exist anymore
        APIResponse verifyRes=apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create()
                        .setHeader("Authorization","Bearer "+token)
                        .setQueryParam("page","1")
                        .setQueryParam("limit","12"));
        Assert.assertTrue(verifyRes.ok(),"Post-delete events list call should succeed");

        List<String>idsAfterDelete=JsonPath.read(verifyRes.text(),"$.data[*].id");
        Assert.assertFalse(idsAfterDelete.contains(eventId),
        "Deleted event must no longer appear in the event list");
        System.out.println("Deletion verified: event no longer in list");
    }
}
