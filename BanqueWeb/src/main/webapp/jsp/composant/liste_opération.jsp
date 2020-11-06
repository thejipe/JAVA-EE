<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<String[]> liste = (ArrayList<String[]>) request.getAttribute("listeOp");
%>

<table id="noOpe">
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
</table>