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
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.PusherProvider" %>
<%@ page import="codeu.view.ComponentProvider" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.util.Stack" %>

<%
  Conversation conversation = (Conversation) request.getAttribute("conversation");
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  ComponentProvider componentProvider = ComponentProvider.getInstance();
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">
  <link rel="stylesheet" href="/css/chat.css">

  <script src="https://js.pusher.com/4.1/pusher.min.js"></script>

  <script type="text/javascript">
    // Pusher code
    var pusher = new Pusher('${PusherProvider.PUSHER_KEY}', {
      cluster: 'us2',
      encrypted: true
    });

    var channel = pusher.subscribe('${PusherProvider.CHAT_CHANNEL}');

    // When notification received
    channel.bind('${PusherProvider.MESSAGE_SENT}', function (data) {
      messages[data.id] = {};
      messages[data.id].view = data.view;
      messages[data.id].children = [];
      messages[data.id].depth = data.parentId == "" ? 0 : messages[data.parentId].depth+1;
      var list = document.getElementById('message-list'+data.parentId);
      list.innerHTML += formatMessageViewGroup(data.id, "");
      scrollChat();
    });

    var messages = {};
    var orderedRootMessages = [];

    function formatMessageViewGroup(messageId, childrenHtml) {
      var message = messages[messageId];
      return "<li style=\"padding-left:" + message.depth*12 + "px;\">" + message.view + "</li>"
          + "<ul id=\"message-list" + messageId + "\">" + childrenHtml + "</ul>"
    }

    function buildSubTree(messageId, depth) {
      messages[messageId].depth = depth;
      var childrenHtml = "";
      messages[messageId].children.forEach(function (childId) {
        childrenHtml += buildSubTree(childId, depth+1);
      });
      return formatMessageViewGroup(messageId, childrenHtml);
    }

    function makeChatTree() {
      var message_list = document.getElementById("message-list");
      orderedRootMessages.forEach(function (value) {
        message_list.innerHTML += buildSubTree(value, 0);
      });
    }

    function setup() {
      <% HashMap<UUID, ArrayList<Message>> messageMap = MessageStore.getInstance().getParentMessageMap(); %>
      <% for(Message message : messages) {
        String view = componentProvider.messageSentInChat(message);
        StringBuilder viewBuilder = new StringBuilder();
        for(char c : view.toCharArray()) {
          if(c == '"')
            viewBuilder.append("\\");
          viewBuilder.append(c);
        }
        String formattedView = viewBuilder.toString(); %>
        messages["<%=message.getId()%>"] = {};
        messages["<%=message.getId()%>"].view = "<%=formattedView%>";
        messages["<%=message.getId()%>"].children = [];
          <% for(Message child : messageMap.get(message.getId())) { %>
            messages["<%=message.getId()%>"].children.push("<%=child.getId()%>");
          <% } %>
      <% } %>
      <% for(Message message : messages) { %>
        <% if(message.getParentId() == null) { %>
          orderedRootMessages.push("<%=message.getId()%>");
        <% } %>
      <% } %>
      makeChatTree();
      scrollChat();
    }

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

  <style>
    #chat div {
      width: auto;
      margin: 0;
    }

    .message-group-view {
      margin-top: 8px;
    }

    .message-group-view div {
      display: inline-block;
      vertical-align: top;
    }

    .profile-image {
      padding: 16px 8px;
    }

    .profile-image img {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      margin: 0 auto;
      display: block;
    }

    .message-header  > * {
      display: inline;
      font-size: xx-small;
    }

    .message-card > div {
      display: block;
    }

    .message-content {
      padding: 2px 10px;
      margin-bottom: 8px;
      background-color: #eee;
      border-radius: 10px;
      cursor: pointer;
    }

    #reply-to {
      display: inline-block;
      width: auto;
      background-color: lightgray;
      margin-left: 10px;
      cursor: default;
    }
  </style>
</head>
<body onload="setup()">

  <%@ include file="../component/navbar.jsp" %>

  <div id="container">

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>

    <div id="chat">
      <ul id="message-list">
      </ul>
    </div>

    <hr/>

      <div class="message-content" id="reply-to"></div>
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
