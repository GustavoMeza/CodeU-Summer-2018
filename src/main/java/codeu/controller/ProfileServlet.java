// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.time.Instant;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the profile page. */
public class ProfileServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  private BlobstoreService blobstoreService;

  /**
   * Set up state for handling profile-related requests. This method is only called when running in a
   * server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setBlobstoreService(BlobstoreServiceFactory.getBlobstoreService());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }
  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
     this.messageStore = messageStore;
   }

  public void setBlobstoreService(BlobstoreService blobstoreService) {
    this.blobstoreService = blobstoreService;
  }

  /**
   * This function fires when a user requests the /profile URL. It simply forwards the request to
   * profile.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String requestUrl = request.getRequestURI();
    String username = requestUrl.substring("/users/".length());
    User user = userStore.getUser(username);

    if (user == null){
      //redirects user to the login page if user is not logged in
      response.sendRedirect("/login");
      return;
    }

    List<Message> messagesByUser = messageStore.getMessagesByUser(user.getId());

    request.setAttribute("messagesByUser", messagesByUser);

    //SETTING LAST LOGIN ATTRIBUTE
    user.setLastLogin(Instant.now());
    request.setAttribute("username", username);
    request.setAttribute("user", user);
    request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");

    User user = userStore.getUser(username);

    if (user == null){
      //redirects user to the login page if user is not logged in
      response.sendRedirect("/login");
      return;
    }

    //SETTING LAST LOGIN ATTRIBUTE
    user.setLastLogin(Instant.now());

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    if (blobKeys == null || blobKeys.isEmpty()) {
      String aboutMeContent = request.getParameter("About Me");
      // this removes any HTML from the message content
      String cleanedAboutMeContent = Jsoup.clean(aboutMeContent, Whitelist.none());
      user.setAboutMe(cleanedAboutMeContent);
    } else {
      user.setAvatarKey(blobKeys.get(0));
    }

    userStore.updateUser(user);

    // redirect to a GET request
    response.sendRedirect("/users/" + username);
  }

}
