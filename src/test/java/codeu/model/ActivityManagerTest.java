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
import com.pusher.rest.data.Result;
import com.pusher.rest.data.Result.Status;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
  private Result mockResult;

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
    mockResult = Mockito.mock(Result.class);
    Mockito.when(mockPusher.trigger(any(String.class), any(), any())).thenReturn(mockResult);
    Mockito.when(mockResult.getStatus()).thenReturn(Status.SUCCESS);

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

    // Message sent through Pusher to Activity feed contains the right information
    ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
    Map<String, String> expectedMessage = new HashMap<>();
    expectedMessage.put("view", "HTML code");

    Mockito.verify(mockPusher).trigger(Mockito.eq(PusherProvider.ACTIVITY_CHANNEL),
        Mockito.eq(PusherProvider.NEW_ACTIVITY),
        mapArgumentCaptor.capture());

    Assert.assertEquals(mapArgumentCaptor.getValue(), expectedMessage);

    // Creating the View for the right Message
    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    Mockito.verify(mockComponentProvider).messageSentInChat(messageArgumentCaptor.capture());

    Assert.assertEquals(messageArgumentCaptor.getValue(), mockMessage);

    // Message sent through Pusher to Chat contains the right information
    expectedMessage = new HashMap<>();
    expectedMessage.put("view", "HTML code");
    expectedMessage.put("id", mockMessage.getId().toString());
    expectedMessage.put("parentId", mockMessage.getParentId().toString());
    mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);

    Mockito.verify(mockPusher).trigger(Mockito.eq(PusherProvider.CHAT_CHANNEL),
        Mockito.eq(PusherProvider.MESSAGE_SENT),
        mapArgumentCaptor.capture());

    Assert.assertEquals(expectedMessage, mapArgumentCaptor.getValue());
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
