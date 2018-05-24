<%@ page import="java.util.UUID" %>

<%@ page import="java.time.Instant" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page import="codeu.model.data.*" %>
<%@ page import="codeu.model.store.basic.*" %>

<%!
// Methods that define the layout for an activity, easy to change!

public static final String errorMessage = "Error loading activity";

// Builds and returns the layout for an activity of the type UserJoined
public String userJoined(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append(formatCreationTime(activity.getFiringTime()));
    sBuilder.append(formatUserName(activity.getObjectId()));
    sBuilder.append(" joined!");
    return sBuilder.toString();
}

// Builds and returns the layout for an activity of the type ConversationCreated
public String conversationCreated(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    UUID conversationId = activity.getObjectId();
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    if(conversation == null) return errorMessage;
    sBuilder.append(formatCreationTime(activity.getFiringTime()));
    sBuilder.append(formatUserName(conversation.getOwnerId()));
    sBuilder.append(" created a new conversation: ");
    sBuilder.append(formatConversation(conversationId));
    return sBuilder.toString();
}

// Builds and returns the layout for an activity of the type MessageSent
public String messageSent(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    UUID messageId = activity.getObjectId();
    Message message = MessageStore.getInstance().getMessage(messageId);
    if(message == null) return errorMessage;
    sBuilder.append(formatCreationTime(activity.getFiringTime()));
    sBuilder.append(formatUserName(message.getAuthorId()));
    sBuilder.append(" sent a message in ");
    sBuilder.append(formatConversation(message.getConversationId()));
    sBuilder.append(": ");
    sBuilder.append(formatMessage(messageId));
    return sBuilder.toString();
}


// Reusable method that returns the layout for the firing time.
public String formatCreationTime(Instant time) {
    // Todo: Change this to show in local time.
    LocalDateTime datetime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
    String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(datetime);
    return String.format("<strong>%s GMT: </strong>", timeStamp.toString());
}

// Reusable method that returns the layout for the user.
public String formatUserName(UUID userId) {
    User user = UserStore.getInstance().getUser(userId);
    if(user == null) return errorMessage;
    return user.getName();
}

// Reusable method that returns the layout for the conversation.
public String formatConversation(UUID conversationId) {
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    if(conversation == null) return errorMessage;
    String conversationLink = String.format("/chat/%s", conversation.getTitle());
    return String.format("<a href=\"%s\">%s</a>", conversationLink, conversation.getTitle());
}

// Reusable method that returns the layout for the message.
public String formatMessage(UUID messageId) {
    Message message = MessageStore.getInstance().getMessage(messageId);
    if(message == null) return errorMessage;
    return String.format("\"%s\"", message.getContent());
}
%>