<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: xell
  Date: 25/10/2020
  Time: 03:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    private int parse_S_I(String v){
        return Integer.parseInt(v) ;
    }
    private int tern_date(boolean test, String vrai, String faux){
        return test ? parse_S_I(vrai) : parse_S_I(faux);
    }
%>

<%
    String[] dateSplit = new SimpleDateFormat("yyyy/MM/dd").format(new Date()).split("/");
    DecimalFormat df = new DecimalFormat("00");
    String rDay, rMonth, rYear, rDayF, rMonthF, rYearF;
    HttpSession sess = request.getSession();
    if (request.getParameterMap().containsKey("jInit")){
        rDay = request.getParameter("jInit"); rMonth = request.getParameter("mInit"); rYear = request.getParameter("yInit");
        rDayF = request.getParameter("jFinal"); rMonthF = request.getParameter("mFinal"); rYearF = request.getParameter("yFinal");
    } else {
        rDay = (String) sess.getAttribute("jInit"); sess.removeAttribute("jInit");
        rMonth = (String) sess.getAttribute("mInit"); sess.removeAttribute("mInit");
        rYear = (String) sess.getAttribute("yInit"); sess.removeAttribute("yInit");
        rDayF = (String) sess.getAttribute("jFinal"); sess.removeAttribute("jFinal");
        rMonthF = (String) sess.getAttribute("mFinal"); sess.removeAttribute("mFinal");
        rYearF = (String) sess.getAttribute("yFinal"); sess.removeAttribute("yFinal");
    }


    int year = tern_date(rYear != null, rYear, dateSplit[0]), month = tern_date(rMonth != null, rMonth, dateSplit[1]), day = tern_date(rDay != null, rDay, dateSplit[2]),
            yearF = tern_date(rYearF != null, rYearF, dateSplit[0]), monthF = tern_date(rMonthF != null, rMonthF, dateSplit[1]), dayF = tern_date(rDayF != null, rDayF, dateSplit[2]);
%>
<td>
    <table style="width: 100%">
        <thead>
        <tr>
            <th colspan="2">
                Liste des opérations réalisées:
            </th>
        </tr>
        </thead>
        <tbody style="text-align: center;">
        <tr>
            <td colspan="2">
                <select id="jInit" name="jInit">
                    <% for (int i = 1; i <= 31; i++) { %>
                    <option value="<%=i%>" <%=i==day ? "selected" : ""%>><%=df.format(i)%></option>
                    <% } %>
                </select>
                <select id="mInit" name="mInit">
                    <% for (int i = 1; i <= 12; i++) { %>
                    <option value="<%=i%>" <%=i==month ? "selected" : ""%>><%=df.format(i)%></option>
                    <% } %>
                </select>
                <select id="aInit" name="aInit">
                    <% for (int i = year-4; i <= year; i++) { %>
                    <option value="<%=i%>" <%=i==year ? "selected" : ""%>><%=i%></option>
                    <% } %>
                </select>
                au
                <select id="jFinal" name="jFinal">
                    <% for (int i = 1; i <= 31; i++) { %>
                    <option value="<%=i%>" <%=i==dayF ? "selected" : ""%>><%=df.format(i)%></option>
                    <% } %>
                </select>
                <select id="mFinal" name="mFinal">
                    <% for (int i = 1; i <= 12; i++) { %>
                    <option value="<%=i%>" <%=i==monthF ? "selected" : ""%>><%=df.format(i)%></option>
                    <% } %>
                </select>
                <select id="aFinal" name="aFinal">
                    <% for (int i = year-4; i <= year; i++) { %>
                    <option value="<%=i%>" <%=i==yearF ? "selected" : ""%>><%=i%></option>
                    <% } %>
                </select>
            </td>
        </tr>
        </tbody>
    </table>
</td>
<td align="center"><button name="action" type="submit" value="extraction">Extraire la liste</button></td>
