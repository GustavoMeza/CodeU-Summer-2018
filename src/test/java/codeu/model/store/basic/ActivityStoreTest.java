package codeu.model.store.basic;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by GustavoMG on 22/05/2018.
 */
public class ActivityStoreTest {

    private ActivityStore activityStore;


    private final Conversation CONVERSATION_ONE =
            new Conversation(
                    UUID.randomUUID(), UUID.randomUUID(), "conversation_one", Instant.ofEpochMilli(2000));
    private final Message MESSAGE_ONE =
            new Message(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "message one",
                    Instant.ofEpochMilli(3000));
    private final User USER_ONE =
            new User(
                    UUID.randomUUID(),
                    "test_username_one",
                    "$2a$10$/zf4WlT2Z6tB5sULB9Wec.QQdawmF0f1SbqBw5EeJg5uoVpKFFXAa",
                    Instant.ofEpochMilli(1000));

    private final Activity ACTIVITY_ONE =
            new Activity(
                    Activity.Type.MessageSent,
                    MESSAGE_ONE.getId(),
                    MESSAGE_ONE.getCreationTime());
    private final Activity ACTIVITY_TWO =
            new Activity(
                    Activity.Type.ConversationCreated,
                    CONVERSATION_ONE.getId(),
                    CONVERSATION_ONE.getCreationTime());
    private final Activity ACTIVITY_THREE =
            new Activity(
                    Activity.Type.UserJoined,
                    USER_ONE.getId(),
                    USER_ONE.getCreationTime());


    @Before
    public void setup() {
        // Currently ActivityStore generates the Activity list from other data stores, so we should mockify them!

        // ConversationStore Mockify
        ConversationStore mockConversationStore = Mockito.mock(ConversationStore.class);
        List<Conversation> conversations = new ArrayList<>();
        conversations.add(CONVERSATION_ONE);
        Mockito.when(mockConversationStore.getAllConversations()).thenReturn(conversations);

        // MessageStore Mockify
        MessageStore mockMessageStore = Mockito.mock(MessageStore.class);
        List<Message> messages = new ArrayList<>();
        messages.add(MESSAGE_ONE);
        Mockito.when(mockMessageStore.getAllMessages()).thenReturn(messages);

        // UserStore Mockify
        UserStore mockUserStore = Mockito.mock(UserStore.class);
        List<User> users = new ArrayList<>();
        users.add(USER_ONE);
        Mockito.when(mockUserStore.getAllUsers()).thenReturn(users);

        // Expected Activity List, it's sorted accordingly.
        final List<Activity> activityList = new ArrayList<>();
        activityList.add(ACTIVITY_ONE);
        activityList.add(ACTIVITY_TWO);
        activityList.add(ACTIVITY_THREE);

        // Setting up the activity store.
        activityStore = ActivityStore.getTestInstance(mockConversationStore, mockUserStore, mockMessageStore);
    }

    @Test
    public void testGetActivities() {
        List<Activity> resultActivities = activityStore.getActivities(0, 5);
        Assert.assertEquals(3, resultActivities.size());
        assertEquals(ACTIVITY_ONE, resultActivities.get(0));
        assertEquals(ACTIVITY_TWO, resultActivities.get(1));
        assertEquals(ACTIVITY_THREE, resultActivities.get(2));
    }

    @Test
    public void testGetActivities_empty() {
        List<Activity> resultActivities = activityStore.getActivities(3, 5);
        Assert.assertEquals(0, resultActivities.size());
    }

    @Test
    public void testGetActivities_badRequest() {
        List<Activity> resultActivities = activityStore.getActivities(4, 3);
        Assert.assertNull(resultActivities);
    }

    private void assertEquals(Activity expectedActivity, Activity actualActivity) {
        Assert.assertEquals(expectedActivity.getType(), actualActivity.getType());
        Assert.assertEquals(expectedActivity.getObjectId(), actualActivity.getObjectId());
        Assert.assertEquals(expectedActivity.getFiringTime(), actualActivity.getFiringTime());
    }
}
