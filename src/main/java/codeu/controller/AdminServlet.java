/** AdminServlet.java
* @author Alana Dillinger
*/

package codeu.controller;

import codeu.model.data.User;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the chat page. */
public class AdminServlet extends ChatHttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Set up state for handling chat requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Loops through the messages and users and counts which user has sent the
   * most messages in all conversations.
   * @return String with the username of the most active user
   */
  public String getMostActiveUser() {
    List<User> users = userStore.getAllUsers();
    int currentCount = 0;
    int maxCount = 0;
    String mostActiveUser =  null;
    for (User user : users) {
      List<Message> userMessages = messageStore.getMessagesFromUser(user);
      currentCount = userMessages.size();
      if(currentCount > maxCount){
        maxCount = currentCount;
        mostActiveUser = user.getName();
      }
    }
    return mostActiveUser;
  }

  /**
   * Loops through the messages and users and counts which user has used the
   * most characters in their messages-the wordiest user
   * @return String with the username of the wordiest user
   */
  public String getWordiestUser() {
    List<User> users = userStore.getAllUsers();
    int currentCount = 0;
    int maxCount = 0;
    String wordiestUser =  null;
    for (User user : users) {
      List<Message> userMessages = messageStore.getMessagesFromUser(user);
      for(Message message : userMessages){
        currentCount = currentCount + message.getContent().length();
      }
      if(currentCount > maxCount){
        maxCount = currentCount;
        wordiestUser = user.getName();
        }
      }
    return wordiestUser;
  }

  /**
   * This function fires when a user navigates to the admin page. It gets the administration title from
   * the URL, finds the most current statistics.
   * It then forwards to admin.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    super.doGet(request, response);

    String username = (String) request.getSession().getAttribute("user");
    String newestUser = userStore.getNewestUser();

    request.setAttribute("dailyUsers", userStore.numberOfDailyUsers());
    request.setAttribute("mostActiveUser", getMostActiveUser());
    request.setAttribute("wordiestUser", getWordiestUser());
    request.setAttribute("newestUser", newestUser);
    request.setAttribute("numberOfUsers", userStore.numberOfUsers());
    request.setAttribute("numberOfConversations", conversationStore.numberOfConversations());
    request.setAttribute("numberOfMessages", messageStore.numberOfMessages());

    if (username == null) {
      // user is not logged in, don't let them add a message
      response.sendRedirect("/login");
      return;
    }else if (!userStore.isUserAdmin(username)) {
      request.setAttribute("error", "That user does not have access.");
      request.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(request, response);
      return;
    }else{
      request.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(request, response);
    }

  }
}
