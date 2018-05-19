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
            instance = new ActivityStore();
        }
        return instance;
    }

    List<Activity> activities;

    /**
     * This class is a singleton, so its constructor is private. Call getInstance() instead.
     */
    private ActivityStore() {
        this.activities = getActivityList();
        activities.sort((Activity a, Activity b) -> -a.getTimeStamp().compareTo(b.getTimeStamp()));
    }

    // Todo: getActivityList should retrieve from PersistentDataStor
    private List<Activity> getActivityList() {
        List<Activity> activities = new ArrayList<>();

        List<Conversation> conversations = ConversationStore.getInstance().getAllConversations();
        for (Conversation conversation : conversations) {
            Activity activity = new Activity(Activity.Type.ConversationCreated, conversation.getId(), conversation.getCreationTime());
            activities.add(activity);
        }

        List<User> users = UserStore.getInstance().getAllUsers();
        for (User user : users) {
            Activity activity = new Activity(Activity.Type.UserJoined, user.getId(), user.getCreationTime());
            activities.add(activity);
        }

        List<Message> messages = MessageStore.getInstance().getAllMessages();
        for (Message message : messages) {
            Activity activity = new Activity(Activity.Type.MessageSent, message.getId(), message.getCreationTime());
            activities.add(activity);
        }

        return activities;
    }

    public List<Activity> getActivities(int offSet, int maxElements) {
        int indexTo = Math.min(activities.size(), offSet + maxElements);
        try {
            return activities.subList(offSet, indexTo);
        } catch (Exception ex) {
            return null;
        }

    }
}
