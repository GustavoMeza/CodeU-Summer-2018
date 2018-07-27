package codeu.controller;

import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.datastore.Blob;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.mockito.internal.matchers.CapturesArguments;

public class ProfileServletTest {
  private ProfileServlet profileServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private MessageStore mockMessageStore;
  private UserStore mockUserStore;
  private BlobstoreService mockBlobstoreService;

  @Before
  public void setup() {
    profileServlet = new ProfileServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockUserStore = Mockito.mock(UserStore.class);
    profileServlet.setUserStore(mockUserStore);

    mockMessageStore = Mockito.mock(MessageStore.class);
    profileServlet.setMessageStore(mockMessageStore);

    mockBlobstoreService = Mockito.mock(BlobstoreService.class);
    profileServlet.setBlobstoreService(mockBlobstoreService);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    // Setup store
    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
            Instant.now());
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    List<Message> fakeMessageList = new ArrayList<>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            fakeUser.getId(),
            UUID.randomUUID(),
            "test message",
            Instant.now()));
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            fakeUser.getId(),
            null,
            "test message 2",
            Instant.now()));
    Mockito.when(mockMessageStore.getMessagesByUser(fakeUser.getId()))
        .thenReturn(fakeMessageList);

    // Setup request
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/users/test_username");

    // Exercise
    profileServlet.doGet(mockRequest, mockResponse);

    // Verify
    Mockito.verify(mockRequest).setAttribute("messagesByUser", fakeMessageList);
    Mockito.verify(mockRequest).setAttribute("username", "test_username");
    Mockito.verify(mockRequest).setAttribute("user", fakeUser);

		Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
    public void testDoGet_UserNotFound() throws IOException, ServletException {
     // Setup
     Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);
     Mockito.when(mockRequest.getRequestURI()).thenReturn("/users/test_username");

     // Exercise
     profileServlet.doGet(mockRequest, mockResponse);

     // Verify
     Mockito.verify(mockResponse).sendRedirect("/login");
   }

   @Test
     public void testDoPost_InvalidUser() throws IOException, ServletException {
      // Setup
      Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);
      Mockito.when(mockRequest.getRequestURI()).thenReturn("/users/test_username");

      // Exercise
      profileServlet.doPost(mockRequest, mockResponse);

      // Verify
      Mockito.verify(mockResponse).sendRedirect("/login");
    }
    @Test
      public void testDoPost_AboutMe() throws IOException, ServletException {
       // Setup
       Mockito.when(mockRequest.getParameter("About Me"))
           .thenReturn("Hi I am D'Nae");
       Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

      Map<String, List<BlobKey>> mockBlobs = new HashMap<>();
      Mockito.when(mockBlobstoreService.getUploads(mockRequest)).thenReturn(mockBlobs);

       User fakeUser = new User(
           UUID.randomUUID(),
           "test_username",
           "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
           Instant.now());
       Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

       //Exercise
       profileServlet.doPost(mockRequest, mockResponse);

       //Verify
       Assert.assertEquals(
           mockUserStore.getUser("test_username").getAboutMe(), "Hi I am D'Nae");
       Mockito.verify(mockResponse).sendRedirect("/users/test_username");
    }

    @Test
    public void testDoPost_Image() throws IOException, ServletException {
      Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

      Map<String, List<BlobKey>> mockBlobs = new HashMap<>();
      List<BlobKey> mockBlobKeys = new ArrayList<>();
      BlobKey mockBlobKey = new BlobKey("xyz");
      mockBlobKeys.add(0, mockBlobKey);
      mockBlobs.put("image", mockBlobKeys);
      Mockito.when(mockBlobstoreService.getUploads(mockRequest)).thenReturn(mockBlobs);

      User fakeUser = new User(
          UUID.randomUUID(),
          "test_username",
          "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
          Instant.now());

      Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

      //Exercise
      profileServlet.doPost(mockRequest, mockResponse);

      //Verify
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      Mockito.verify(mockUserStore).updateUser(userCaptor.capture());
      Assert.assertEquals(userCaptor.getValue().getAvatarKey().getKeyString(), mockBlobKey.getKeyString());
      Mockito.verify(mockResponse).sendRedirect("/users/test_username");
    }

     @Test
     public void testDoPost_CleansHtmlContent() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("About Me"))
        .thenReturn("Contains <b>html</b> and <script>JavaScript</script> content.");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");


       Map<String, List<BlobKey>> mockBlobs = new HashMap<>();
       Mockito.when(mockBlobstoreService.getUploads(mockRequest)).thenReturn(mockBlobs);

    User fakeUser = new User(
        UUID.randomUUID(),
        "test_username",
        "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
        Instant.now());
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    profileServlet.doPost(mockRequest, mockResponse);

    Assert.assertEquals(
        mockUserStore.getUser("test_username").getAboutMe(), "Contains html and  content.");
    Mockito.verify(mockResponse).sendRedirect("/users/test_username");
  }
}
