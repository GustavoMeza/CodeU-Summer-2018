<%@ page import="codeu.model.data.User" %>
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.time.ZonedDateTime" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%
  String username = (String) request.getAttribute("username");
  User user = (User) request.getAttribute("user");
  List<Message> messagesByUser = (List<Message>) request.getAttribute("messagesByUser");
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= username %> Profile Page</title>
  <link rel="stylesheet" href="/css/main.css">

  <style>
    textarea {
      width:100%;
    }
  </style>

  <style>
    #chat {
      background-color: white;
      height: 500px;
      width: 750px;
      overflow-y: scroll
    }
  </style>

  <script>
    function scrollChat(){
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    }
  </script>

</head>
<body>

<%@ include file="../component/navbar.jsp" %>
<%@ include file="../component/activity-helper.jsp" %>

<div id="container">
  <h1><%= username %>'s Profile Page</h1>
  <hr/>

  <% String avatarUrl = user.getAvatarImage(); %>

  <img src="<%=avatarUrl == null ? "/css/default-profile.png" : avatarUrl%>" alt="profile-image">
  <!-- Ensures that the user who is logged in is the only one able to update Profile Image-->

  <% if(request.getSession().getAttribute("user") != null) { %>
  <% if(request.getSession().getAttribute("user").equals(request.getAttribute("username"))) { %>
  <form action="<%= blobstoreService.createUploadUrl("/users/" + username) %>" method="POST" enctype="multipart/form-data">
    <div class="form-group">
      <label>Upload image</label>
      <!-- Allow only image file types to be selected -->
      <input type="file" name="image" accept="image/*">
    </div>
    <button type="submit">Save</button>
  </form>
  <% } %>
  <% } %>

  <h2>About <%= username %></h2>


  <p>
    <%= user.getAboutMe()%>
  </p>
  <!-- Ensures that the user who is logged in is the only one able to update About Me Content-->
  <% if(request.getSession().getAttribute("user") != null) { %>
  <% if(request.getSession().getAttribute("user").equals(request.getAttribute("username"))) { %>
  <h3>Edit your About Me (Only you can see this)</h3>

  <form action="/users/<%= username %>" method="POST">
    <textarea name="About Me" cols="40" rows="5"> </textarea>
    <br/>
    <button type="submit">Update</button>
  </form>
  <% } %>
  <% } %>



  <hr/>
  <h1><%= username %>'s Sent Messages</h1>

  <div id="chat">
    <ul>
      <% for (Message message : messagesByUser) {
      %>
      <li>
        <%= formatCreationTime(message.getCreationTime()) %>
        <%= formatMessage(message.getId()) %>
        in
        <%= formatConversation(message.getConversationId()) %>
      </li>


      <% }   %>

    </ul>
  </div>


  <hr/>


</div>
</body>
</html>
