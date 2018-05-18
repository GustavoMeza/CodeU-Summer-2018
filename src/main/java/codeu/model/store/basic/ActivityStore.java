package codeu.model.store.basic;

import codeu.model.data.*;

import java.util.*;

/**
 * Creation by GustavoMG on 17/05/2018.
 */
public class ActivityStore {
    /** Singleton instance of ConversationStore. */
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

    /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
    private ActivityStore() {
        List<Conversation> conversations = ConversationStore.getInstance().getAllConversations();
        Map<UUID, Conversation> conversationMap= new HashMap<>();
        for(Conversation conversation : conversations) conversationMap.put(conversation.getId(), conversation);

        List<User> users = UserStore.getInstance().getAllUsers();
        Map<UUID, User> userMap = new HashMap<>();
        for(User user : users) userMap.put(user.getId(), user);

        List<Message> messages = MessageStore.getInstance().getAllMessages();

        List<Creation> creationList = new ArrayList<>();
        creationList.addAll(conversations);
        creationList.addAll(users);
        creationList.addAll(messages);
        creationList.sort((Creation a, Creation b)->-a.getCreationTime().compareTo(b.getCreationTime()));

        Activity.Builder activityBuilder = Activity.Builder.getInstance();
        activities = new ArrayList<>();
        for (Creation creation : creationList) {
            if(creation.getClass() == User.class) {
                User user = (User) creation;
                Activity activity = activityBuilder.userJoined(user);
                activities.add(activity);
            }
            if(creation.getClass() == Conversation.class) {
                Conversation conversation = (Conversation) creation;
                User user = userMap.get(conversation.getOwnerId());
                Activity activity = activityBuilder.conversationCreated(user, conversation);
                activities.add(activity);
            }
            if(creation.getClass() == Message.class) {
                Message message = (Message) creation;
                User user = userMap.get(message.getAuthorId());
                Conversation conversation = conversationMap.get(message.getConversationId());
                Activity activity = activityBuilder.messageSent(user, conversation, message);
                activities.add(activity);
            }
        }
    }

    public List<Activity> getActivities(int offSet, int maxElements) {
        int indexTo = Math.min(activities.size(), offSet+maxElements);
        return activities.subList(offSet, indexTo);
    }

}
