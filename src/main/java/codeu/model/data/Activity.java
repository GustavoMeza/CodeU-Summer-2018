package codeu.model.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Creation by GustavoMG on 17/05/2018.
 */
public class Activity {

    public final Type type;
    public final List<UUID> params;

    private Activity(Type type) {
        this.type = type;
        params = new ArrayList<>();
    }

    public enum Type {
        UserJoined, ConversationCreatd, MessageSent
    }

    /*
    public static class Builder {
        private static Builder instance;

        public static Builder getInstance() {
            if(instance == null) {
                instance = new Builder();
            }
            return instance;
        }

        private Activity activity;

        public Activity userJoined(User user) {
            activity = new Activity();
            addInstant(user.getCreationTime());
            addUser(user);
            addText(" Joined!");
            return activity;
        }

        public Activity conversationCreated(User user, Conversation conversation) {
            activity = new Activity();
            addInstant(conversation.getCreationTime());
            addUser(user);
            addText(" created a new conversation: ");
            addConversation(conversation);
            return activity;
        }

        public Activity messageSent(User user, Conversation conversation, Message message) {
            activity = new Activity();
            addInstant(message.getCreationTime());
            addUser(user);
            addText(" sent a message in ");
            addConversation(conversation);
            addText(String.format(": \"%s\"", message.getContent()));
            return activity;
        }

        private void addInstant(Instant instant) {
            addChunk(instant.toString(), null);
        }

        private void addConversation(Conversation conversation) {
            addChunk(conversation.getTitle(), "/chat/"+conversation.getTitle());
        }

        private void addUser(User user) {
            addChunk(user.getName(), null);
        }

        private void addText(String text) {
            addChunk(text, null);
        }

        private void addChunk(String text, String action) {
            activity.chunks.add(new Chunk(text, action));
        }
    }
    */
}
