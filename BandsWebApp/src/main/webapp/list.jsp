<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<body>

<table border="1" cellpadding="5">
    <thead>
    <tr>
        <th>Nazev kapely</th>
        <th>Styly</th>
        <th>Region</th>
        <th>Cena za hodinu</th>
        <th>Hodnoceni</th>
    </tr>
    </thead>
    <c:forEach items="${bands}" var="band">
        <tr>
            <%--<form method="post" action="${pageContext.request.contextPath}/bands/update">--%>
                <%--<td><input name="name" type="text" value="${band.name}" /></td>--%>
                <%--<td><input name="style" type="text" value="${band.style}"/></td>--%>
                <%--<td><input name="region" type="text" value="${band.region}"/></td>--%>
                <%--<td><input name="pricePerHour" type="text" value="${band.pricePerHour}"/></td>--%>
                <%--<td><input name="rate" type="text" value="${band.rate}"/></td>--%>
                <%--<input type="hidden" name="id" value="${band.id}"/>--%>
                <%--<td><input type="submit" value="Update"/></td>--%>
            <%--</form>--%>
            <%--<td><form method="post" action="${pageContext.request.contextPath}/bands/delete?id=${band.id}"--%>
                      <%--style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>--%>
            <td align="center"><c:out value="${band.name}"/></td>
            <td align="center"><c:out value="${band.styles.toString()}"/></td>
            <td align="center"><c:out value="${band.region}"/></td>
            <td align="center"><c:out value="${band.pricePerHour}"/></td>
            <td align="center"><c:out value="${band.rate}"/></td>

            <td align="left" width="5%"><form method="post" action="${pageContext.request.contextPath}/bands/update?id=${band.id}"
                                              style="margin-bottom: 0;"><input type="submit" value="Upravit"></form></td>

            <td align="left" width="5%"><form method="post" action="${pageContext.request.contextPath}/bands/delete?id=${band.id}"
                                              style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Zadejte kapelu</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/bands/add" method="post">
    <table>
        <tr>
            <th>Nazev kapely:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Styly:</th>
            <td><input type="text" name="style" value="<c:out value='${param.style}'/>"/></td>
        </tr>
        <tr>
            <th>Region:</th>
            <td><input type="text" name="region" value="<c:out value='${param.region}'/>"/></td>
        </tr>
        <tr>
            <th>Cena za hodinu:</th>
            <td><input type="text" name="pricePerHour" value="<c:out value='${param.pricePerHour}'/>"/></td>
        </tr>
        <tr>
            <th>Rate:</th>
            <td><input type="text" name="rate" value="<c:out value='${param.rate}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Zadat" />
</form>

</body>
</html>
