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
<!DOCTYPE html>
<html>
<head>
  <title>CodeU Chat App</title>
  <link rel="stylesheet" href="/css/loginpage.css">
</head>
<body>

  <nav>
    <div class="nav-wrapper">
        <b id="navTitle" href="/ center">CODEU CHAT APP</b>
    </div>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">


      <form action="/login" method="POST">
        <label for="username">USERNAME: </label>
        <br/>
        <input type="text" name="username" id="username">
        <br/>
        <label for="password">PASSWORD: </label>
        <br/>
        <input type="password" name="password" id="password">
        <br/><br/>
        <button type="/login">LOGIN</button>
        <br/><br/>
      </form>

      <p>Not a member? <br/> <a href="/register">Sign Up</a></p>
    </div>
  </div>
</body>
</html>
