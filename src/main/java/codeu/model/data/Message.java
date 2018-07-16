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

package codeu.model.data;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * Class representing a message. Messages are sent by a User in a Conversation.
 */
public class Message {

  private final UUID id;
  private final UUID conversation;
  private final UUID author;
  private final UUID parent;
  private List<Message> children;
  private final String content;
  private final Instant creation;

  /**
   * Constructs a new Message.
   *
   * @param id the ID of this Message
   * @param conversation the ID of the Conversation this Message belongs to
   * @param author the ID of the User who sent this Message
   * @param content the text content of this Message
   * @param creation the creation time of this Message
   */
  public Message(UUID id, UUID conversation, UUID author, UUID parent, String content,
      Instant creation) {
    this.id = id;
    this.conversation = conversation;
    this.author = author;
    this.parent = parent;
    this.children = new ArrayList<Message>();
    this.content = content;
    this.creation = creation;
  }

  /**
   * Returns the ID of this Message.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the ID of the Conversation this Message belongs to.
   */
  public UUID getConversationId() {
    return conversation;
  }

  /**
   * Returns the ID of the User who sent this Message.
   */
  public UUID getAuthorId() {
    return author;
  }

  /**
   * Returns the ID of the Message which this is a reply to, null if no such Message exists
   **/
  public UUID getParentId() {
    return parent;
  }

  /**
   * Returns the ID of the reply to this message, null if no such message exists
   **/
  public List<Message> getChildren(){
    return children;
  }

  /**
   * Set the ID of the message that replies to this message
   **/
  public void addChild(Message message){
    this.children.add(message);
  }

  /**
   * Returns the text content of this Message.
   */
  public String getContent() {
    return content;
  }

  /**
   * Returns the creation time of this Message.
   */
  public Instant getCreationTime() {
    return creation;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    Message other = (Message)obj;
    return other.getId().equals(this.getId()) &&
        other.getConversationId().equals(this.getConversationId()) &&
        other.getAuthorId().equals(this.getAuthorId()) &&
        (other.getParentId() == null && this.getParentId() == null ||
          other.getParentId().equals(this.getParentId())) &&
        other.getContent().equals(this.getContent()) &&
        other.getCreationTime().equals(this.getCreationTime());
  }
}
