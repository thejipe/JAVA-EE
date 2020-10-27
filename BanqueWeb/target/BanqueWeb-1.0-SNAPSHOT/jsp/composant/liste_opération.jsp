<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: xell
  Date: 25/10/2020
  Time: 05:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<String[]> liste = (ArrayList<String[]>) request.getAttribute("listeOp");
%>

<table id="noOpe">
    <% if (liste.isEmpty()) { %>
    <p style="border: 1px solid white; padding: 10px; text-align: center; color: red; max-width: max-content;">Il n'y a aucun résultat</p>
    <% } else { %>
        <thead>
        <tr>
            <td align="center">Date :</td>
            <td align="center">Opération :</td>
            <td align="center">Valeur :</td>
        </tr>
        </thead>
        <tbody>
        <% for (String[] s : liste) { %>
        <tr>
            <td align="center"><%=s[0]%></td>
            <td align="center"><%=s[1].equals("+") ?"Crédit(+)":"Débit(-)"%></td>
            <td align="center"><%=s[2]%></td>
        </tr>
        <% } %>
        </tbody>
    <% } %>
</table>