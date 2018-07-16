<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <link rel="stylesheet" href="/css/chat.css">

  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    }

    // triggered on reply button click
    function reply(id, html) {
      // set the parent parameter for post request
      var parent_input = document.getElementById('parent');
      if(parent_input != null) {
        parent_input.setAttribute("value", id);
      }

      // set focus to textbox, makes easier for the user to reply.
      var message_input = document.getElementById('message');
      if(message_input != null) {
        message_input.focus();
      }

      // button to disable the reply.
      var container = document.getElementById('reply-to');
      if(html != null) {
        html += " <button onclick='reply(\"\", null)' class='transparent'>x</button>";
      }

      container.innerHTML = html;
    }
  </script>
</head>
<body onload="scrollChat()">

  <%@ include file="../component/activity-helper.jsp" %>

  <%@ include file="../component/navbar.jsp" %>

  <%!
      /**
       * Method that creates a layout for a message in the Chat View, including response-to part.
       * @param message The message to be formatted.
       * @return String with the message layout in Chat View.
       */
      public String formatMessageInChat(Message message) {
        MessageStore messageStore = MessageStore.getInstance();
        StringBuilder formattedChat = new StringBuilder();

        if(message.getParentId() == null){
          String formattedMessage = String.format("<div class=\"parent\">%s</div>&#8618;",
                  formatMessagePartInChat(message));
          formattedChat.append(formattedMessage);
          if(message.getChildren() != null){
            List<Message> childrenMessages = message.getChildren();
            for(Message nextMessage : childrenMessages){
              formatMessageInChat(nextMessage);
            }
          }
          // Reply label to be shown as information to send messages
          String replyLabel = formatMessagePartInChat(message);

          // Label is going to be passed as a string argument, so it should be processed
          // for instance <a href="/">link</a> should be <a href=\"/\">link</a>
          StringBuilder replyLabelArgument = new StringBuilder();
          for(char c : replyLabel.toCharArray()) {
              if(c == '\"') replyLabelArgument.append("\\");
              replyLabelArgument.append(c);
          }

          // Button part of layout
          String replyButton = String.format(
                  "<button onclick='reply(\"%s\", \"%s\")' class='transparent'>&#8618;</button>",
                  message.getId().toString(),
                  replyLabelArgument.toString());
          formattedChat.append(replyButton);
        }else{
          //formatMessageInChat(messageStore.getMessage(message.getParentId()));
            // Message part of layout
          formattedChat.append(formatMessagePartInChat(message));
        }

          return formattedChat.toString();
      }

      /**
       * Method that creates the layout part only for a message in the Chat View.
       * @param message The message to be formatted.
       * @return String with the message layout in Chat View.
       */
      public String formatMessagePartInChat(Message message) {
          return String.format("%s: %s",
                  formatUserName(message.getAuthorId()),
                  message.getContent());
      }
  %>

  <div id="container">

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>

    <div id="chat">
      <ul>
    <%
      for (Message message : messages) {
        if(message.getParentId() == null){
          out.print("<li>");
          out.print(formatMessageInChat(message));
          out.print("</li>");
        }
      }
    %>
      </ul>
    </div>

    <hr/>

      <div class="parent" id="reply-to"></div>
    <% if (request.getSession().getAttribute("user") != null) { %>
    <form action="/chat/<%= conversation.getTitle() %>" method="POST">
        <input type="hidden" name="parent" id="parent">
        <input type="text" placeholder="new message" name="message" id="message">
        <button type="submit">Send</button>
    </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>

  </div>

</body>
</html>
