<%@ page import="gestionsErreurs.MessagesDErreurs" %>
<%@ page import="java.util.Arrays" %>
<%--
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
        .decimal { width: 10%; }
        .entier { width: 30%; }
        body {
            background-color: #3E2626;
            color: white;
            font-family: "monospace", serif;
        }
        input { font-size: 20px; }
        thead { background-color: lightcoral; }
        #info td {
            border: thin solid white;
        }
        td {
            min-width: 100px;
            padding: 3px 10px 3px 10px;
        }
        table { border-collapse: collapse; }
    </style>
</head>
<body>

<%
    String statut = (String) request.getSession().getAttribute("statut"), errMsg = MessagesDErreurs.getMessageDErreur((String) request.getAttribute("Erreur")), resultMsg = (String) request.getAttribute("result");
%>

<center>
    <jsp:include page="composant/info_compte.jsp"/>
    <br>
    <form action="Operations" method="post">
        <table>
            <tbody>
                <tr><jsp:include page="composant/operation_input.jsp"><jsp:param name="statut" value="<%=statut%>"/></jsp:include></tr>
                <br>
                <tr><jsp:include page="composant/dates_selector.jsp"/></tr>
            </tbody>
        </table>
    </form>

    <% switch (statut) {
        case "succes_traitement" : %> <p style="color: lightblue"> <%=resultMsg%> </p> <% break;
        case "error" : %> <p style="color: red"> <%=errMsg%> <% break;
    } %>

</center>

</body>
</html>
