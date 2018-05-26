package codeu.model.data;

import java.time.Instant;
import java.util.UUID;

/**
 * Creation by GustavoMG on 17/05/2018.
 * Class representing an activity, which can be thought of as a notification when something important occurred.
 */
public class Activity {

    /**
     * Enum that represents the type of activity.
     */
    // This might be extended in the future to support other types of activities.
    // Setting up a finite amount of types helps us to know how to handle them.
    public enum Type {
        UserJoined, ConversationCreated, MessageSent
    }

    private final Type type;
    private final UUID objectId;
    private final Instant createdAt;

    /**
     * Constructs a new Activity.
     *
     * @param type the type of activity
     * @param objectId the ID of the object that the activity refers to.
     * @param createdAt the instant when the activity was fired.
     */
    public Activity(Type type, UUID objectId, Instant createdAt) {
        this.type = type;
        this.objectId = objectId;
        this.createdAt = createdAt;
    }

    /** Returns the type of activity */
    public Type getType() {
        return type;
    }

    /** Return the id of the object that the activity refers to */
    public UUID getObjectId() {
        return objectId;
    }

    /** Returns the instant when the activity was fired */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
