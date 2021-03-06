<%--
  admin.jsp
  Alana Dillinger
--%>
<!DOCTYPE html>
<html>
<head>
  <title>Administration</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

<%@ include file="../component/navbar.jsp" %>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>App Usage Statistics</h1>

      <% if(request.getAttribute("error") != null){ %>
          <h2 style="color:red"><%= request.getAttribute("error") %></h2>
      <% } else{ %>
          <p>Number of Active Users Today: <%= request.getAttribute("dailyUsers") %></p>
          <p>Number of Users: <%= request.getAttribute("numberOfUsers") %></p>
          <p>Number of Conversations: <%= request.getAttribute("numberOfConversations") %></p>
          <p>Number of Messages: <%= request.getAttribute("numberOfMessages") %></p>
          <p>Most Active User: <%= request.getAttribute("mostActiveUser") %></p>
          <p>Newest User: <%= request.getAttribute("newestUser") %></p>
          <p>Wordiest User: <%= request.getAttribute("wordiestUser") %></p>
      <% } %>
    </div>
  </div>
</body>
</html>
