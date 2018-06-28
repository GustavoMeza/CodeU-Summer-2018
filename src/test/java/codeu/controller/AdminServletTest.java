// AdminServletTest.java
// Alana Dillinger

package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class AdminServletTest {

  private AdminServlet adminServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private MessageStore mockMessageStore;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    adminServlet = new AdminServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/admin.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    adminServlet.setConversationStore(mockConversationStore);

    mockMessageStore = Mockito.mock(MessageStore.class);
    adminServlet.setMessageStore(mockMessageStore);

    mockUserStore = Mockito.mock(UserStore.class);
    adminServlet.setUserStore(mockUserStore);
  }

  @Test
  public void testDoGet_noUser() throws IOException, ServletException {
    String fakeNewestUser = mockUserStore.getNewestUser();

    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn(null);

    adminServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("numberOfUsers", mockUserStore.numberOfUsers());
    Mockito.verify(mockRequest).setAttribute("numberOfMessages", mockMessageStore.numberOfMessages());
    Mockito.verify(mockRequest).setAttribute("numberOfConversations", mockConversationStore.numberOfConversations());
    Mockito.verify(mockRequest).setAttribute("mostActiveUser", adminServlet.getMostActiveUser());
    Mockito.verify(mockRequest).setAttribute("wordiestUser", adminServlet.getWordiestUser());
    Mockito.verify(mockRequest).setAttribute("newestUser", fakeNewestUser);

    Mockito.verify(mockResponse).sendRedirect("/login");
  }



  @Test
  public void testDoGet_notAdminUser() throws IOException, ServletException {
    String fakeNewestUser = mockUserStore.getNewestUser();

    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("not-admin");

    adminServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("numberOfUsers", mockUserStore.numberOfUsers());
    Mockito.verify(mockRequest).setAttribute("numberOfMessages", mockMessageStore.numberOfMessages());
    Mockito.verify(mockRequest).setAttribute("numberOfConversations", mockConversationStore.numberOfConversations());
    Mockito.verify(mockRequest).setAttribute("mostActiveUser", adminServlet.getMostActiveUser());
    Mockito.verify(mockRequest).setAttribute("wordiestUser", adminServlet.getWordiestUser());
    Mockito.verify(mockRequest).setAttribute("newestUser", fakeNewestUser);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }




  @Test
  public void testDoGet_adminUser() throws IOException, ServletException {
    String fakeNewestUser = mockUserStore.getNewestUser();

    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("admin");

    adminServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("numberOfUsers", mockUserStore.numberOfUsers());
    Mockito.verify(mockRequest).setAttribute("numberOfMessages", mockMessageStore.numberOfMessages());
    Mockito.verify(mockRequest).setAttribute("numberOfConversations", mockConversationStore.numberOfConversations());
    Mockito.verify(mockRequest).setAttribute("mostActiveUser", adminServlet.getMostActiveUser());
    Mockito.verify(mockRequest).setAttribute("wordiestUser", adminServlet.getWordiestUser());
    Mockito.verify(mockRequest).setAttribute("newestUser", fakeNewestUser);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
