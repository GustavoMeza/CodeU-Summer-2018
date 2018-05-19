<%@ page import="java.util.UUID" %>

<%@ page import="java.time.Instant" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page import="codeu.model.data.*" %>
<%@ page import="codeu.model.store.basic.*" %>

<%!
public String userJoined(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append(formatCreationTime(activity.getTimeStamp()));
    sBuilder.append(formatUserName(activity.getObjectId()));
    sBuilder.append(" joined!");
    return sBuilder.toString();
}

public String conversationCreated(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    UUID conversationId = activity.getObjectId();
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    sBuilder.append(formatCreationTime(activity.getTimeStamp()));
    sBuilder.append(formatUserName(conversation.getOwnerId()));
    sBuilder.append(" created a new conversation: ");
    sBuilder.append(formatConversation(conversationId));
    return sBuilder.toString();
}

public String messageSent(Activity activity) {
    StringBuilder sBuilder = new StringBuilder();
    UUID messageId = activity.getObjectId();
    Message message = MessageStore.getInstance().getMessage(messageId);
    sBuilder.append(formatCreationTime(activity.getTimeStamp()));
    sBuilder.append(formatUserName(message.getAuthorId()));
    sBuilder.append(" sent a message in ");
    sBuilder.append(formatConversation(message.getConversationId()));
    sBuilder.append(": ");
    sBuilder.append(formatMessage(messageId));
    return sBuilder.toString();
}



public String formatCreationTime(Instant time) {
    LocalDateTime datetime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
    String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(datetime);
    return String.format("<strong>%s GMT: </strong>", timeStamp.toString());
}

public String formatUserName(UUID userId) {
    User user = UserStore.getInstance().getUser(userId);
    return user.getName();
}

public String formatConversation(UUID conversationId) {
    Conversation conversation = ConversationStore.getInstance().getConversation(conversationId);
    String conversationLink = String.format("/chat/%s", conversation.getTitle());
    return String.format("<a href=\"%s\">%s</a>", conversationLink, conversation.getTitle());
}

public String formatMessage(UUID messageId) {
    Message message = MessageStore.getInstance().getMessage(messageId);
    return String.format("\"%s\"", message.getContent());
}
%>