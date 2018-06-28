package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by GustavoMG on 22/05/2018.
 */
public class ActivityServletTest {

    private ActivityServlet activityServlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockRequestDispatcher;
    private ActivityStore mockActivityStore;

    @Before
    public void setup() {
        activityServlet = new ActivityServlet();

        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/activities.jsp"))
                .thenReturn(mockRequestDispatcher);

        mockActivityStore = Mockito.mock(ActivityStore.class);
        activityServlet.setActivityStore(mockActivityStore);
    }

    @Test
    public void testDoGet() throws IOException, ServletException {
        List<Activity> fakeActivityList = new ArrayList<>();
        fakeActivityList.add(
                new Activity(
                        Activity.Type.MessageSent,
                        UUID.randomUUID(),
                        Instant.now()));

        Mockito.when(mockActivityStore.getActivities(0, ActivityServlet.maxActivitiesLoaded))
                .thenReturn(fakeActivityList);

        activityServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockRequest).setAttribute("activities", fakeActivityList);
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoGet_nullActivityList() throws IOException, ServletException {
        List<Activity> fakeActivityList = null;

        Mockito.when(mockActivityStore.getActivities(0, ActivityServlet.maxActivitiesLoaded))
                .thenReturn(fakeActivityList);

        activityServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockRequest).setAttribute("activities", fakeActivityList);
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }
}
