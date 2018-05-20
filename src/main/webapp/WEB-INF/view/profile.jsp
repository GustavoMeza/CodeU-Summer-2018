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
<%
String username = (String) request.getAttribute("username");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= username %> Profile Page</title>
  <link rel="stylesheet" href="/css/main.css">

  <style>
  textarea
  {
    width:100%;
  }
  </style>

</head>
<body>

  <%@ include file="../component/navbar.jsp" %>

  <div id="container">
    <h1><%= username %> Profile Page</h1>
    <hr/>
    <h2>About <%= username %></h2>

    <p> I like dogs and cats
    </p>

    <h3>Edit your About Me (Only you can see this)</h3>


    <form action="/conversations" method="POST">
        <div class="form-group">

        <textarea name="Text1" cols="40" rows="5"></textarea>
      </div>

      <button type="submit">Submit</button>
    </form>
    <hr/>


  </div>
</body>
</html>
