package codeu.model;

import codeu.model.data.Activity;
import codeu.model.data.Activity.Type;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.view.ComponentProvider;
import com.pusher.rest.Pusher;
import com.pusher.rest.data.Result;
import com.pusher.rest.data.Result.Status;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivityManager {

  /** Singleton instance */
  private static ActivityManager instance;

  /**
   * Static method to get the singleton instance
   * @return An ActivityManager
   */
  public static ActivityManager getInstance() {
    if(instance == null) {
      instance = new ActivityManager(UserStore.getInstance(), MessageStore.getInstance(),
          ConversationStore.getInstance(), PusherProvider.getService(),
          ComponentProvider.getInstance(), ActivityStore.getInstance());
    }
    return instance;
  }

  /**
   * Static method to get the singleton instance, use only for testing
   * @return An ActivityManager
   */
  public static ActivityManager getTestInstance(UserStore userStore, MessageStore messageStore,
      ConversationStore conversationStore, Pusher pusher,
      ComponentProvider componentProvider, ActivityStore activityStore) {
    return new ActivityManager(userStore, messageStore, conversationStore, pusher,
        componentProvider, activityStore);
  }

  /** Store class that gives access to Users. */
  private final UserStore userStore;

  /** Store class that gives access to Messages. */
  private final MessageStore messageStore;

  /** Store class that gives access to Conversations. */
  private final ConversationStore conversationStore;

  /** The Pusher instance that will real-time two-way communicate through Pusher API*/
  private final Pusher pusher;

  /** View class that gives view components for UI update */
  private final ComponentProvider componentProvider;

  /** Store class that gives access to Activities. */
  private final ActivityStore activityStore;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private ActivityManager(UserStore userStore, MessageStore messageStore,
      ConversationStore conversationStore, Pusher pusher,
      ComponentProvider componentProvider, ActivityStore activityStore) {
    this.userStore = userStore;
    this.messageStore = messageStore;
    this.conversationStore = conversationStore;
    this.pusher = pusher;
    this.componentProvider = componentProvider;
    this.activityStore = activityStore;
  }

  /**
   * Method that concentrates things to be done when a user joined.
   * @param user The user that just joined.
   */
  public void userJoined(User user) {
    userStore.addUser(user);
    Activity activity = new Activity(Type.UserJoined, user.getId(), user.getCreationTime());
    activityStore.addActivity(activity);
    String view = componentProvider.userJoined(activity);
    pusher.trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
        Collections.singletonMap("view", view));
  }

  /**
   * Method that concentrates things to be done when a conversation is created.
   * @param conversation The conversation that was just created.
   */
  public void conversationCreated(Conversation conversation) {
    conversationStore.addConversation(conversation);
    Activity activity = new Activity(Type.ConversationCreated, conversation.getId(),
        conversation.getCreationTime());
    activityStore.addActivity(activity);
    String view = componentProvider.conversationCreated(activity);
    pusher.trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
        Collections.singletonMap("view", view));
  }

  /**
   * Method that concentrates things to be done when a message is sent.
   * @param message The message that was just sent.
   */
  public void messageSent(Message message) {
    messageStore.addMessage(message);
    Activity activity = new Activity(Type.MessageSent, message.getId(), message.getCreationTime());
    activityStore.addActivity(activity);
    String view = componentProvider.messageSent(activity);
    for(int i = 0; i < 4; i++) {
      Result pusherResult = pusher.trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
          Collections.singletonMap("view", view));
      if(pusherResult.getStatus() == Status.SUCCESS) {
        break;
      }
    }
    view = componentProvider.messageSentInChat(message);
    Map<String, String> map = new HashMap<>();
    map.put("view", view);
    map.put("id", message.getId().toString());
    map.put("parentId", message.getParentId() == null ? "" : message.getParentId().toString());
    pusher.trigger(PusherProvider.CHAT_CHANNEL, PusherProvider.MESSAGE_SENT, map);
  }
}
