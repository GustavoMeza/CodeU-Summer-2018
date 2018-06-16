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
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">

  <style>
    .parent {
      font-size: x-small;
      margin-bottom: -6px;
    }

    button.transparent {
      border: 0;
      padding: 0;
      margin-left: 8px;
      background: none;
      cursor: pointer;
    }

    li {
      margin: 4px;
    }

    ul {
      list-style-type: none;
      margin: 0;
      padding: 0;
    }

    #chat {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    // scroll the chat div to the bottom
    var idReply = null;

    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    }

    function reply(id, html) {
      var parent_input = document.getElementById('parent');
      if(parent_input != null) {
        parent_input.setAttribute("value", id);
      }

      var message_input = document.getElementById('message');
      if(message_input != null) {
        message_input.focus();
      }

      var container = document.getElementById('reply-to');
      if(html != null) {
        html += " <button onclick='reply(\"\", null)' class='transparent'>X</button>";
      }
      container.innerHTML = html;
    }
  </script>
</head>
<body onload="scrollChat()">

  <%@ include file="../component/activity-helper.jsp" %>

  <%@ include file="../component/navbar.jsp" %>

  <%!
    public String formatChatMessage(Message message) {
      return String.format("%s: %s",
              formatUserName(message.getAuthorId()),
              message.getContent());
    }

    public String formatHtml(String string) {
      StringBuilder stringBuilder = new StringBuilder();
      for(char c : string.toCharArray()) {
        if(c == '\"') stringBuilder.append("\\");
        stringBuilder.append(c);
      }
      return stringBuilder.toString();
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
        out.print("<li>");
        if(message.getParentId() != null) {
          out.print("<div class=\"parent\">");
          Message parent = MessageStore.getInstance().getMessage(message.getParentId());
          out.print(formatChatMessage(parent));
          out.print("</div>");
          out.print("&#8618;");
        }
        out.print(formatChatMessage(message));
        String replyButton =String.format(
            "<button onclick='reply(\"%s\", \"%s\")' class='transparent'>&#8618;</button>",
            message.getId().toString(),
            formatHtml(formatChatMessage(message)));
        out.print(replyButton);
        out.print("</li>");
      }
    %>
      </ul>
    </div>

    <hr/>

      <div class="parent" id="reply-to"></div>
    <% if (request.getSession().getAttribute("user") != null) { %>
    <form action="/chat/<%= conversation.getTitle() %>" method="POST">
        <input type="hidden" name="parent" id="parent">
        <input type="text" name="message" id="message">
        <br/>
        <button type="submit">Send</button>
    </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>

  </div>

</body>
</html>
