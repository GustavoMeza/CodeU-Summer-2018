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
<%@ page import="codeu.model.data.Activity" %>
<%
List<Activity> activities = (List<Activity>) request.getAttribute("activities");
%>

<!DOCTYPE html>
<html>
<head>
  <script src="moment.js"></script>
  <script>
  System.import('moment.js');
      moment().fromNow();
    </script>
  <title>Activity</title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">

  <style>
    #activity-board {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

</head>
<body>
  <%@ include file="../component/activity-helper.jsp" %>

  <%@ include file="../component/navbar.jsp" %>

  <div id="container">

    <h1>Activity<a href="" style="float: right">&#8635;</a></h1>
    <hr/>

    <div id="activity-board">
      <ul>
      <% if(activities == null) {
          out.print("Internal error: List not loaded correctly");
        } else {
          for (Activity activity : activities) {
          out.print("<li>");
          // Easy to add new types of activities and change layouts.
          switch (activity.getType()) {
            case UserJoined:
              out.print(userJoined(activity));
              break;
            case ConversationCreated:
              out.print(conversationCreated(activity));
              break;
            case MessageSent:
              out.print(messageSent(activity));
              break;
          }
          out.print("</li>");
        }
      } %>
      </ul>
    </div>

    <hr/>
  </div>

</body>
</html>
