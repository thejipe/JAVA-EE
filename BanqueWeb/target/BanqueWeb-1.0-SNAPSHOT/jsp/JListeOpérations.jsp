<%@ page import="gestionsErreurs.MessagesDErreurs" %>
<%@ page import="java.util.Arrays" %><%--
  Created by IntelliJ IDEA.
  User: xell
  Date: 25/10/2020
  Time: 04:37
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
        #noOpe {
            border: 1px solid white;
            padding: 5px;
        }
    </style>
</head>
<body>

<%
    String tmp = (String) request.getSession().getAttribute("statut"), errMsg = (String) request.getAttribute("Erreur"), resultMsg = (String) request.getAttribute("result");
    request.getParameterMap().forEach((k,v) -> request.getSession().setAttribute(k, Arrays.asList(v).get(0)));
%>

<center>
    <jsp:include page="composant/info_compte.jsp"/> <br>
    <jsp:include page="composant/liste_opération.jsp"/> <br>
    <form action="Operations" method="post" style="padding: 10px; display: table; text-align: center;">
        <button name="action" type="submit" value="retour">Retour</button>
    </form>

    <% if (tmp != null && tmp.equals("old") && resultMsg != null){ %> <p style="color: blue"> <%=resultMsg%> </p> <% }
    if (tmp != null && tmp.equals("error") && errMsg != null) { %> <p style="color: red"> <%=MessagesDErreurs.getMessageDErreur(errMsg)%> </p> <% } %>
</center>

</body>
</html>
