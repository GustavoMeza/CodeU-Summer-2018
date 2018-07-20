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
<%@ page import="codeu.model.PusherProvider" %>
<%@ page import="codeu.view.ComponentProvider" %>

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

  <script src="https://js.pusher.com/4.1/pusher.min.js"></script>
  <script type="text/javascript">
    var pusher = new Pusher('${PusherProvider.PUSHER_KEY}', {
      cluster: 'us2',
      encrypted: true
    });

    var channel = pusher.subscribe('${PusherProvider.CHAT_CHANNEL}');

    channel.bind('${PusherProvider.MESSAGE_SENT}', function (data) {
      var list = document.getElementById('message-list');
      list.innerHTML += '<li>' + data.view + '</li>';
      scrollChat();
    });

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

  <%@ include file="../component/navbar.jsp" %>

  <div id="container">

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>

    <div id="chat">
      <ul id="message-list">
    <%
      ComponentProvider componentProvider = ComponentProvider.getInstance();
      for (Message message : messages) {
        if(message.getParentId() == null){
          MessageStore messageStore = MessageStore.getInstance();
          HashMap<UUID, ArrayList<Message>> allMessages = messageStore.getParentMessageMap();

          ArrayList<Message> childrenMessages = allMessages.get(message.getId());
          String formattedMessage = String.format("<div class=\"parent\">%s</div>&#8618;",
                formatMessagePartInChat(messageStore.getMessage(message.getId())));
          out.print(componentProvider.messageSentInChat(message));
          for(Message reply : childrenMessages){
            out.print("<li>");
            out.print(componentProvider.messageSentInChat(message));
            out.print("<li>");
          }
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
