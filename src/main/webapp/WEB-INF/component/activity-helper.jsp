<%@ page import="java.util.UUID" %>
<%@ page import="java.util.Date" %>

<%@ page import="org.ocpsoft.prettytime.PrettyTime" %>

<%@ page import="java.time.Instant" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.ZonedDateTime" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page import="codeu.model.data.*" %>
<%@ page import="codeu.model.store.basic.*" %>

<%!
    // Methods that define the layout for an activity, easy to change!

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
        sBuilder.append(" joined!");
        sBuilder.append(" " + formatCreationTime(activity.getCreatedAt()));
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


    // Reusable methods that let you create consistent layouts through the application, easily updatable.

    /**
     * Method that returns the part of the layout that refers to creation time.
     * @param time The time that's going to be displayed in the layout.
     * @return String with the creation time layout section
     */
    public String formatCreationTime(Instant time) {
/*

        // Todo: Change this to show in local time.
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        //LocalDateTime datetime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
        ZonedDateTime zdt = time.atZone( zoneId ) ;
        String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(zdt);
        return String.format("<strong>%s PST: </strong>", timeStamp.toString());
*/
        Date dateCreated = Date.from(time);
        PrettyTime relativeTime = new PrettyTime();
        return relativeTime.format(dateCreated);


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
%>
