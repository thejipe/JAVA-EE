<%@ page import="gestionsErreurs.MessagesDErreurs" %><%--
  Created by IntelliJ IDEA.
  User: xell
  Date: 20/10/2020
  Time: 21:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ESIPE Bank</title>
    <style>
        body {
            background-color: #3E2626;
            color: white;
            font-family: "monospace", serif;
        }
        input { font-size: 20px; }
        thead { background-color: lightcoral; }
        td {
            min-width: 100px;
            padding: 3px 10px 3px 10px;
            border: thin solid white;
        }
        table { border-collapse: collapse; }
    </style>
</head>

<body>
<div style="text-align: center;">
    <h3>Saisie du numéro de compte :</h3>
    <form method="post" action="Operations">
        <label for="no_compt">Entrez le N° de compte</label>
        <input type=text id="no_compt" name="no_compt"/>
        <button type="submit">Consulter</button>
    </form>
    <%if (request.getMethod().equals("POST")) { %>
    <h1 style="color: red"><%=MessagesDErreurs.getMessageDErreur((String) request.getAttribute("Erreur"))%></h1>
    <% } %>
</div>
</body>
</html>
