<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    HttpSession sess = request.getSession();
    String op = null, valL = null, valR = null;
    if (request.getParameterMap().containsKey("op")){
        op = request.getParameter("op"); valL = request.getParameter("entier"); valR = request.getParameter("decimal");
    } else if (sess.getAttribute("op") != null){
        op = (String) sess.getAttribute("op"); //sess.removeAttribute("op");
        valL = (String) sess.getAttribute("entier"); //sess.removeAttribute("entier");
        valR = (String) sess.getAttribute("decimal"); //sess.removeAttribute("decimal");
    }
    boolean error = request.getParameter("statut").equals("error");
    String checkedP = op != null && op.equals("+") ? "checked" : "", checkedM = op != null && op.equals("-") ? "checked" : "";
    String entier = !error && valL != null ? valL : "", decimal = !error && valR != null ? valR : "";
%>

<td>
    <table>
        <thead>
        <tr>
            <th align="center">Opération à effectuer</th>
            <th align="center">Valeur</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td align="center">
                <label for="plus">(+)</label>
                <input type="radio" id="plus" name="op" value="+" <%=checkedP%>>
                <input type="radio" id="moins" name="op" value="-" <%=checkedM%>>
                <label for="moins">(-)</label>
            </td>
            <td align="center">
                <input class="entier" type=text id="entier" name="entier" maxlength="10" value="<%=entier%>"/>
                ,
                <input class="decimal" type=text id="decimal" maxlength="2" name="decimal" value="<%=decimal%>"/>
            </td>

        </tr>
        </tbody>
    </table>
</td>
<td align="center"><button name="action" type="submit" value="traitement">Traiter</button></td>