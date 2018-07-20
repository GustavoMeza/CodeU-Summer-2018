package codeu.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

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
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ActivityManagerTest {

  private ActivityManager activityManager;
  private UserStore mockUserStore;
  private MessageStore mockMessageStore;
  private ConversationStore mockConversationStore;
  private Pusher mockPusher;
  private ComponentProvider mockComponentProvider;
  private ActivityStore mockActivityStore;

  private User mockUser = new User(UUID.randomUUID(), "Juan", "HashedPassword",
      Instant.now());
  private Message mockMessage = new Message(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
      UUID.randomUUID(), "Message!", Instant.now());
  private Conversation mockConversation = new Conversation(UUID.randomUUID(), UUID.randomUUID(),
      "Title!", Instant.now());

  @Before
  public void setup() {
    mockUserStore = Mockito.mock(UserStore.class);
    mockMessageStore = Mockito.mock(MessageStore.class);
    mockConversationStore = Mockito.mock(ConversationStore.class);
    mockPusher = Mockito.mock(Pusher.class);
    mockComponentProvider = Mockito.mock(ComponentProvider.class);
    mockActivityStore = Mockito.mock(ActivityStore.class);

    activityManager = ActivityManager.getTestInstance(mockUserStore, mockMessageStore,
        mockConversationStore, mockPusher, mockComponentProvider, mockActivityStore);
  }

  @Test
  public void userJoined() {
    Activity expectedActivity = new Activity(Type.UserJoined, mockUser.getId(),
        mockUser.getCreationTime());
    Mockito.when(mockComponentProvider.userJoined(Mockito.any(Activity.class)))
        .thenReturn("HTML code");

    activityManager.userJoined(mockUser);

    Mockito.verify(mockUserStore).addUser(mockUser);
    ArgumentCaptor<Activity> activityArgumentCaptor = ArgumentCaptor.forClass(Activity.class);
    Mockito.verify(mockActivityStore).addActivity(activityArgumentCaptor.capture());
    Mockito.verify(mockComponentProvider).userJoined(activityArgumentCaptor.capture());
    for(Activity actualActivity : activityArgumentCaptor.getAllValues()) {
      Assert.assertEquals(actualActivity, expectedActivity);
    }
    Mockito.verify(mockPusher).trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
        Collections.singletonMap("view", "HTML code"));
  }

  @Test
  public void messageSent() {
    Activity expectedActivity = new Activity(Type.MessageSent, mockMessage.getId(),
        mockMessage.getCreationTime());
    Mockito.when(mockComponentProvider.messageSent(Mockito.any(Activity.class)))
        .thenReturn("HTML code");
    Mockito.when(mockComponentProvider.messageSentInChat(Mockito.any(Message.class)))
        .thenReturn("HTML code");

    activityManager.messageSent(mockMessage);

    Mockito.verify(mockMessageStore).addMessage(mockMessage);
    ArgumentCaptor<Activity> activityArgumentCaptor = ArgumentCaptor.forClass(Activity.class);
    Mockito.verify(mockActivityStore).addActivity(activityArgumentCaptor.capture());
    Mockito.verify(mockComponentProvider).messageSent(activityArgumentCaptor.capture());
    for(Activity actualActivity : activityArgumentCaptor.getAllValues()) {
      Assert.assertEquals(actualActivity, expectedActivity);
    }
    Mockito.verify(mockPusher).trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
        Collections.singletonMap("view", "HTML code"));

    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(mockComponentProvider).messageSentInChat(messageArgumentCaptor.capture());
    Assert.assertEquals(messageArgumentCaptor.getValue(), mockMessage);
    Mockito.verify(mockPusher).trigger(PusherProvider.CHAT_CHANNEL, PusherProvider.MESSAGE_SENT,
        Collections.singletonMap("view", "HTML code"));
  }

  @Test
  public void conversationCreated() {
    Activity expectedActivity = new Activity(Type.ConversationCreated, mockConversation.getId(),
        mockConversation.getCreationTime());
    Mockito.when(mockComponentProvider.conversationCreated(Mockito.any(Activity.class)))
        .thenReturn("HTML code");

    activityManager.conversationCreated(mockConversation);

    Mockito.verify(mockConversationStore).addConversation(mockConversation);
    ArgumentCaptor<Activity> activityArgumentCaptor = ArgumentCaptor.forClass(Activity.class);
    Mockito.verify(mockActivityStore).addActivity(activityArgumentCaptor.capture());
    Mockito.verify(mockComponentProvider).conversationCreated(activityArgumentCaptor.capture());
    for(Activity actualActivity : activityArgumentCaptor.getAllValues()) {
      Assert.assertEquals(actualActivity, expectedActivity);
    }
    Mockito.verify(mockPusher).trigger(PusherProvider.ACTIVITY_CHANNEL, PusherProvider.NEW_ACTIVITY,
        Collections.singletonMap("view", "HTML code"));
  }

}
