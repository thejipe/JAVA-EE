<%--
  Created by IntelliJ IDEA.
  User: xell
  Date: 25/10/2020
  Time: 02:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<table id="info">
    <thead>
        <tr>
            <td align="center">N° Compte</td>
            <td align="center">Nom</td>
            <td align="center">Prenom</td>
            <td align="center">Solde</td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center"><%=session.getAttribute("noCompte")%></td>
            <td align="center"><%=session.getAttribute("nom")%></td>
            <td align="center"><%=session.getAttribute("prenom")%></td>
            <td align="center"><%=session.getAttribute("solde")%></td>
        </tr>
    </tbody>
</table>
