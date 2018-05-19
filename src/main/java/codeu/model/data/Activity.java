package codeu.model.data;

import java.time.Instant;
import java.util.UUID;

/**
 * Creation by GustavoMG on 17/05/2018.
 */
public class Activity {

    public enum Type {
        UserJoined, ConversationCreated, MessageSent
    }

    public final Type type;
    public final UUID objectId;
    public final Instant timeStamp;

    public Activity(Type type, UUID objectId, Instant timeStamp) {
        this.type = type;
        this.objectId = objectId;
        this.timeStamp = timeStamp;
    }

    public Type getType() {
        return type;
    }

    public UUID getObjectId() {
        return objectId;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }
}
