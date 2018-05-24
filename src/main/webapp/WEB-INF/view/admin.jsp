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

      <h1>This will be the Administration page</h1>

      <% if(request.getAttribute("error") != null){ %>
          <h2 style="color:red"><%= request.getAttribute("error") %></h2>
      <% } %>


    </div>
  </div>
</body>
</html>