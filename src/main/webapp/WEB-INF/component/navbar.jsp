<nav>
  <% String requestUrl = request.getRequestURI(); %>
    <a id="navTitle" href="/"> CodeU Chat App</a>
    <% if (request.getSession().getAttribute("user") != null) { %>
      <a>Hello <%=request.getSession().getAttribute("user")%>!</a>
      <% if (request.getSession().getAttribute("user").equals("admin")) { %>
        <% if(requestUrl.equals("/WEB-INF/view/admin.jsp")){%>
           <b href="/admin">Admin</b>
        <% }else{%>
           <a href="/admin">Admin</a>
        <% } %>
      <% } %>
    <% } else { %>
      <a href="/login">Login</a>
    <% } %>


    <% if(requestUrl.equals("/WEB-INF/view/activities.jsp")){%>
         <b href="/activity">Activity</b>
    <% }else{%>
         <a href="/activity">Activity</a>
    <% } %>

    <% if(requestUrl.equals("/WEB-INF/view/conversations.jsp") || requestUrl.equals("/WEB-INF/view/chat.jsp")){%>
         <b href="/conversations">Conversations</b>
    <% }else{%>
         <a href="/conversations">Conversations</a>
    <% } %>

    <% if(requestUrl.equals("/WEB-INF/view/profile.jsp")){%>
         <b href="/users/<%=request.getSession().getAttribute("user")%>">My Profile</b>
    <% }else{%>
         <a href="/users/<%=request.getSession().getAttribute("user")%>">My Profile</a>
    <% } %>

    <% if(requestUrl.equals("/about.jsp")){%>
         <b href="/about.jsp">About</b>
    <% }else{%>
         <a href="/about.jsp">About</a>
    <% } %>

    <% if(requestUrl.equals("/logout.jsp")){%>
         <b href="/logout.jsp">Logout</b>
    <% }else{%>
         <a href="/logout.jsp">Logout</a>
    <% } %>
</nav>
