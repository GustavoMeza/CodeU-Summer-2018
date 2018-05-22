package codeu.model.store.basic;

import codeu.model.data.*;

import java.util.*;

/**
 * Creation by GustavoMG on 17/05/2018.
 */
public class ActivityStore {
    /**
     * Singleton instance of ConversationStore.
     */
    private static ActivityStore instance;

    /**
     * Returns the singleton instance of ConversationStore that should be shared between all servlet
     * classes. Do not call this function from a test; use getTestInstance() instead.
     */
    public static ActivityStore getInstance() {
        if (instance == null) {
            instance = new ActivityStore(ConversationStore.getInstance(), UserStore.getInstance(), MessageStore.getInstance());
        }
        return instance;
    }

    /**
     * Instance getter function used for testing.
     * @param conversationStore mock conversation store to use
     * @param userStore mock user store to use
     * @param messageStore mock message store to use
     * @return an instance of ActivityStore for testing.
     */
    public static ActivityStore getTestInstance(ConversationStore conversationStore,
        UserStore userStore, MessageStore messageStore) {
        return new ActivityStore(conversationStore, userStore, messageStore);
    }

    /** The in-memory list of Activities. */
    private List<Activity> activities;
    private ConversationStore conversationStore;
    private UserStore userStore;
    private MessageStore messageStore;

    /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
    private ActivityStore(ConversationStore conversationStore, UserStore userStore, MessageStore messageStore) {
        this.conversationStore = conversationStore;
        this.messageStore = messageStore;
        this.userStore = userStore;
    }

    /**
     * This method generates the list with all activities, and sorts it from most recent to oldest.
     */
    // Todo: should retrieve from PersistentDataStore not generate every time.
    private void generateActivityList() {
        // Slow approach, should be changed
        List<Activity> activities = new ArrayList<>();

        // Generate ConversationCreated activities
        List<Conversation> conversations = conversationStore.getAllConversations();
        for (Conversation conversation : conversations) {
            Activity activity = new Activity(Activity.Type.ConversationCreated, conversation.getId(), conversation.getCreationTime());
            activities.add(activity);
        }

        // Generate UserJoined activities
        List<User> users = userStore.getAllUsers();
        for (User user : users) {
            Activity activity = new Activity(Activity.Type.UserJoined, user.getId(), user.getCreationTime());
            activities.add(activity);
        }

        // Generate MessageSent activities
        List<Message> messages = messageStore.getAllMessages();
        for (Message message : messages) {
            Activity activity = new Activity(Activity.Type.MessageSent, message.getId(), message.getCreationTime());
            activities.add(activity);
        }

        // Sort from most recent to oldest, expensive if many activities!
        activities.sort((Activity a, Activity b)->-a.getFiringTime().compareTo(b.getFiringTime()));

        // Would never be null
        this.activities = activities;
    }

    /**
     * This method will return a portion of the activity list, since returning all the list is expensive!
     * @param offSet First position in the activity list to be returned.
     * @param maxElements Amount of elements to be returned if sufficient exist.
     * @return if request makes sense will return the requested portion of list, null otherwise.
     */
    public List<Activity> getActivities(int offSet, int maxElements) {
        generateActivityList(); // Super bad idea!
        int indexTo = Math.min(activities.size(), offSet + maxElements);
        if(offSet > maxElements) return null;
        return activities.subList(offSet, indexTo);
    }
}
