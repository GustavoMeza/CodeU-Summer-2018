package codeu.controller;

import codeu.model.ActivityManager;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;

public class RegisterServletTest {

  private RegisterServlet registerServlet;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private UserStore mockUserStore;
  private ActivityManager mockActivtyManager;

  @Before
  public void setup() {
    registerServlet = new RegisterServlet();
    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/register.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockUserStore = Mockito.mock(UserStore.class);
    registerServlet.setUserStore(mockUserStore);

    mockActivtyManager = Mockito.mock(ActivityManager.class);
    registerServlet.setActivityManager(mockActivtyManager);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    registerServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_BadUsername() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("bad !@#$% username");

    registerServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest)
        .setAttribute("error", "Please enter only letters, numbers, and spaces.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_NewUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test username");
    Mockito.when(mockRequest.getParameter("password")).thenReturn("test password");

    Mockito.when(mockUserStore.isUserRegistered("test username")).thenReturn(false);

    registerServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    Mockito.verify(mockActivtyManager).userJoined(userArgumentCaptor.capture());
    Assert.assertEquals("test username", userArgumentCaptor.getValue().getName());
    Assert.assertThat(
        userArgumentCaptor.getValue().getPasswordHash(), CoreMatchers.containsString("$2a$10$"));
    Assert.assertEquals(60, userArgumentCaptor.getValue().getPasswordHash().length());

    Mockito.verify(mockResponse).sendRedirect("/login");
  }

  @Test
  public void testDoPost_ExistingUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test username");

    Mockito.when(mockUserStore.isUserRegistered("test username")).thenReturn(true);

    registerServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockActivtyManager, Mockito.never()).userJoined(Mockito.any(User.class));
    Mockito.verify(mockRequest).setAttribute("error", "That username is already taken.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
