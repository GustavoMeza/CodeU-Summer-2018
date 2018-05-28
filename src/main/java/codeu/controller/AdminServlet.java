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
public class AdminServlet extends HttpServlet {

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
   * This function fires when a user navigates to the admin page. It gets the administration title from
   * the URL, finds the most current statistics.
   * It then forwards to admin.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");

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

  /**
   * This function fires when a user submits the form on the chat page. It gets the logged-in
   * username from the session, the conversation title from the URL, and the chat message from the
   * submitted form data. It creates a new Message from that data, adds it to the model, and then
   * redirects back to the chat page.
   */
  /*@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        //This function will handle adding in new data to the chat app, but isn't needed for the prototype
  }*/
}
