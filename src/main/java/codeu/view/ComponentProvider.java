package codeu.view;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ComponentProvider {

  /** Singleton instance */
  private static ComponentProvider instance = new ComponentProvider();

  /**
   * Static method that returns the singleton instance
   * @return A ComponentProvider
   */
  public static ComponentProvider getInstance() {
    return instance;
  }

  /**
   * Method that returns the layout of an activity of the type "UserJoined".
   * @param activity The activity that we are going to construct the layout.
   * @return String with the layout of the activity.
   */
  public String userJoined(Activity activity) {
    UUID userId = activity.getObjectId();
    User user = UserStore.getInstance().getUser(userId);
    if (user == null) {
      String errorMessage = String.format("User %s not found", userId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append(formatUserName(userId));
    sBuilder.append(" joined! ");
    sBuilder.append(formatCreationTime(activity.getCreatedAt()));
    return sBuilder.toString();
  }

  /**
   * Method that returns the layout of an activity of the type "ConversationCreated".
   * @param activity The activity that we are going to construct the layout.
   * @return String with the layout of the activity.
   */
  public String conversationCreated(Activity activity) {
    UUID conversationId = activity.getObjectId();
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    if (conversation == null) {
      String errorMessage = String.format("Conversation %s not found", conversationId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append(formatUserName(conversation.getOwnerId()));
    sBuilder.append(" created a new conversation: ");
    sBuilder.append(formatConversation(conversationId));
    sBuilder.append(" " + formatCreationTime(activity.getCreatedAt()));
    return sBuilder.toString();
  }

  /**
   * Method that returns the layout of an activity of the type "MessageSent".
   * @param activity The activity that we are going to construct the layout.
   * @return String with the layout of the activity.
   */
  public String messageSent(Activity activity) {
    UUID messageId = activity.getObjectId();
    Message message = MessageStore.getInstance().getMessage(messageId);
    if (message == null) {
      String errorMessage = String.format("Message %s not found", messageId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append(formatUserName(message.getAuthorId()));
    sBuilder.append(" sent a message in ");
    sBuilder.append(formatConversation(message.getConversationId()));
    sBuilder.append(": ");
    sBuilder.append(formatMessage(messageId));
    sBuilder.append(" " + formatCreationTime(activity.getCreatedAt()));
    return sBuilder.toString();
  }

  /**
   * Method that creates a layout for a message in the Chat View, including response-to part.
   * @param message The message to be formatted.
   * @return String with the message layout in Chat View.
   */
  public String messageSentInChat(Message message) {
    String imageUrl = "http://www.personalbrandingblog.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640-300x300.png";

    // Button part of layout
    String onclick = String.format("onclick='reply(\"%s\", \"%s\")'",
        message.getId(), message.getContent());

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("<div class=\"message-group-view\">");
      stringBuilder.append("<div class=\"profile-image\">");
        stringBuilder.append("<img src=\"" + imageUrl + "\" class=\"avatar\">");
      stringBuilder.append("</div>");
      stringBuilder.append("<div class=\"message-card\">");
        stringBuilder.append("<div class=\"message-header\">");
          stringBuilder.append(formatUserName(message.getAuthorId()));
          stringBuilder.append(" <span>" + formatCreationTime(message.getCreationTime()) + "</span>");
        stringBuilder.append("</div>");
        stringBuilder.append("<div class=\"message-content\" " + onclick + ">");
          stringBuilder.append(message.getContent());
        stringBuilder.append("</div>");
      stringBuilder.append("</div>");
    stringBuilder.append("</div>");

    return stringBuilder.toString();
  }


  // Reusable methods that let you create consistent layouts through the application, easily updatable.

  /**
   * Method that returns the part of the layout that refers to creation time.
   * @param time The time that's going to be displayed in the layout.
   * @return String with the creation time layout section
   */
  public String formatCreationTime(Instant time) {
    // Todo: Change this to show in local time.
    Date datetime = Date.from(time);
    PrettyTime formattedTime = new PrettyTime();
		return formattedTime.format(datetime);
  }

  /**
   * Method that returns the part of the layout that refers to user name.
   * @param userId The id of the user that's going to be displayed in the layout.
   * @return String with the user name layout section
   */
  public String formatUserName(UUID userId) {
    User user = UserStore.getInstance().getUser(userId);
    if (user == null) {
      String errorMessage = String.format("User %s not found", userId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    String userLink = String.format("/users/%s", user.getName());
    return String.format("<a href=\"%s\">%s</a>", userLink, user.getName());
  }

  /**
   * Method that returns the part of the layout that refers to conversation title.
   * @param conversationId The id of the conversation that's going to be displayed in the layout.
   * @return String with the conversation title layout section
   */
  public String formatConversation(UUID conversationId) {
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    if (conversation == null) {
      String errorMessage = String.format("Conversation %s not found", conversationId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    String conversationLink = String.format("/chat/%s", conversation.getTitle());
    return String.format("<a href=\"%s\">%s</a>", conversationLink, conversation.getTitle());
  }

  /**
   * Method that returns the part of the layout that refers to message content.
   * @param messageId The id of the message that's going to be displayed in the layout.
   * @return String with the message content layout section
   */
  public String formatMessage(UUID messageId) {
    Message message = MessageStore.getInstance().getMessage(messageId);
    if (message == null) {
      String errorMessage = String.format("Message %s not found", messageId.toString());
      throw new IllegalArgumentException(errorMessage);
    }

    return String.format("\"%s\"", message.getContent());
  }

  /**
   * Method that creates the layout part only for a message in the Chat View.
   * @param message The message to be formatted.
   * @return String with the message layout in Chat View.
   */
  public String formatMessageInChat(Message message) {
    return String.format("%s: %s",
        ComponentProvider.getInstance().formatUserName(message.getAuthorId()),
        message.getContent());
  }
}
