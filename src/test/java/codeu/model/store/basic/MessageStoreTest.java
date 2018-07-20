package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MessageStoreTest {

  private MessageStore messageStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final UUID CONVERSATION_ID_ONE = UUID.randomUUID();
  private final UUID MESSAGE_ONE_ID = UUID.randomUUID();
  private final Message MESSAGE_ONE =
      new Message(
          MESSAGE_ONE_ID,
          CONVERSATION_ID_ONE,
          UUID.randomUUID(),
          UUID.randomUUID(),
          "message one",
          Instant.ofEpochMilli(1000));
  private final Message MESSAGE_TWO =
      new Message(
          UUID.randomUUID(),
          CONVERSATION_ID_ONE,
          UUID.randomUUID(),
          UUID.randomUUID(),
          "message two",
          Instant.ofEpochMilli(2000));
  private final Message MESSAGE_THREE =
      new Message(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          MESSAGE_ONE_ID,
          "message three",
          Instant.ofEpochMilli(3000));

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    messageStore = MessageStore.getTestInstance(mockPersistentStorageAgent);

    final List<Message> messageList = new ArrayList<>();
    messageList.add(MESSAGE_ONE);
    messageList.add(MESSAGE_TWO);
    messageList.add(MESSAGE_THREE);
    messageStore.setMessages(messageList);
  }

  @Test
  public void testGetMessagesInConversation() {
    List<Message> resultMessages = messageStore.getMessagesInConversation(CONVERSATION_ID_ONE);

    Assert.assertEquals(2, resultMessages.size());
    Assert.assertEquals(MESSAGE_ONE, resultMessages.get(0));
    Assert.assertEquals(MESSAGE_TWO, resultMessages.get(1));
  }

  @Test
  public void testAddMessage() {
    UUID inputConversationId = UUID.randomUUID();
    Message inputMessage =
        new Message(
            UUID.randomUUID(),
            inputConversationId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            "test message",
            Instant.now());

    messageStore.addMessage(inputMessage);
    Message resultMessage = messageStore.getMessagesInConversation(inputConversationId).get(0);

    Assert.assertEquals(inputMessage, resultMessage);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputMessage);
  }

  @Test
  public void testAddParentChildMessages() {
    UUID parentMessageId = UUID.randomUUID();
    UUID conversationId = UUID.randomUUID();
    Message parentMessage =
        new Message(
            parentMessageId,
            UUID.randomUUID(),
            conversationId,
            null,
            "parent message",
            Instant.now());
    Message childMessageOne =
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            conversationId,
            parentMessageId,
            "child message one",
            Instant.now());

    Message childMessageTwo =
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            conversationId,
            parentMessageId,
            "child message two",
            Instant.now());

    messageStore.addMessage(parentMessage);
    messageStore.addMessage(childMessageOne);
    messageStore.addMessage(childMessageTwo);

    HashMap<UUID, ArrayList<Message>> messagesByParentIdMap = messageStore.getParentMessageMap();
    ArrayList<Message> childrenMessages = new ArrayList<Message>();
    childrenMessages.add(childMessageOne);
    childrenMessages.add(childMessageTwo);
    Assert.assertEquals(messagesByParentIdMap.get(parentMessageId), childrenMessages);

    Message childOneInMap = messagesByParentIdMap.get(parentMessageId).get(0);
    Assert.assertEquals(childOneInMap, childMessageOne);

    Message childTwoInMap = messagesByParentIdMap.get(parentMessageId).get(1);
    Assert.assertEquals(childTwoInMap, childMessageTwo);

    Mockito.verify(mockPersistentStorageAgent).writeThrough(parentMessage);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(childMessageOne);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(childMessageTwo);
  }

  @Test
  public void testGetMessage_ById_found() {
    Message message = messageStore.getMessage(MESSAGE_ONE.getId());

    Assert.assertEquals(message, MESSAGE_ONE);
  }

  @Test
  public void testGetMessage_ById_notFound() {
    Message message = messageStore.getMessage(UUID.randomUUID());

    Assert.assertNull(message);
  }
}
