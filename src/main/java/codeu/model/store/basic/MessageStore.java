// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.lang.Object;
import java.util.HashMap;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;
  /** The in-memory map of Messages with parents and children messages defined. */
  private HashMap<UUID, ArrayList<Message>> messagesMap;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
    messagesMap = new HashMap<UUID, ArrayList<Message>>();
  }

  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    persistentStorageAgent.writeThrough(message);
    ArrayList<Message> children;
    // add message to the HashMap
    if(message.getParentId() != null){
      if(messagesMap.containsKey(message.getParentId())){
        children = messagesMap.get(message.getParentId());
      }else{
        children = new ArrayList<Message>();
      }
      children.add(message);
      messagesMap.replace(message.getParentId(), children);
    }else{// message does not have a parent, start a new key value set
      messagesMap.put(message.getId(), new ArrayList<Message>());
    }
    //persistentStorageAgent.writeThrough(messagesMap);
  }

/** Returns the HashMap with the children associated with their parent */
public HashMap<UUID, ArrayList<Message>> getChildrenMessages(){
  return messagesMap;
}

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId().equals(conversationId)) {
        messagesInConversation.add(message);
      }
    }
    return messagesInConversation;
  }
  public List<Message> getMessagesByUser(UUID id){

    List<Message> messagesByUser = new ArrayList<>();
    for(Message message : messages){
      if(message.getAuthorId().equals(id)){
        messagesByUser.add(message);
      }
    }
    return messagesByUser;
  }

  // /** Access the current users set of Messages.
  //  * @return the users messages in an ArrayList.
  //  */
  // public ArrayList<Message> getMessagesFromUser(User user){
  //   ArrayList<Message> userMessages = new ArrayList<Message> ();
  //   for(Message message : messages){
  //     String author = UserStore.getInstance()
  //       .getUser(message.getAuthorId()).getName();
  //     if(author.equals(user.getName())){
  //          userMessages.add(message);
  //        }
  //      }
  //   return userMessages;
  // }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
    //messagesMap = new HashMap<UUID, ArrayList<Message>>();
    ArrayList<Message> children;;
    for(Message message : messages){
      if(message.getParentId() != null){
        if(messagesMap.containsKey(message.getParentId())){
          children = messagesMap.get(message.getParentId());
        }else{
          children = new ArrayList<Message>();
        }
        children.add(message);
        messagesMap.replace(message.getParentId(), children);
      }else{// message does not have a parent, start a new key value set
        messagesMap.put(message.getId(), new ArrayList<Message>());
      }
    }
  }

  /*public void setMessagesMap(HashMap<UUID, ArrayList<Message>> messagesMap){
    this.messagesMap = messagesMap;
  }*/

  /** Returns all the messages, use with caution. */
  public List<Message> getAllMessages() {
      return messages;
    }

  /** Returns the total number of messages stored*/
  public int numberOfMessages(){
      return messages.size();
    }

  /** Finds and returns the Message with the given Id */
  public Message getMessage(UUID messageId) {
    // This approach will be pretty slow if we have many Messages.
    for(Message message : messages) {
      if(message.getId().equals(messageId)) {
        return message;
      }
    }
    return null;
  }
}
