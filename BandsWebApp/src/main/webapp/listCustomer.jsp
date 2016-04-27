<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<body>

<table border="2" width="100%" bordercolor="Maroon" align="center">
    <thead>
    <tr>
        <th align="center" bgcolor="#a9a9a9">Meno</th>
        <th align="center" bgcolor="#a9a9a9">Telefon</th>
        <th align="center" bgcolor="#a9a9a9">Adresa</th>
    </tr>
    </thead>
    <c:forEach items="${customers}" var="customer">
        <tr>
            <td align="center"><c:out value="${customer.name}"/></td>
            <td align="center"><c:out value="${customer.phoneNumber}"/></td>
            <td align="center"><c:out value="${customer.address}"/></td>

            <td align="left" width="5%"><form method="post" action="${pageContext.request.contextPath}/customers/showUpdate?id=${customer.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Upravit"></form></td>

            <td align="left" width="5%"><form method="post" action="${pageContext.request.contextPath}/customers/delete?id=${customer.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>

<c:choose>
    <c:when test="${not empty param.id}">
        <h2>Upravit</h2>
        <form action="${pageContext.request.contextPath}/customers/update" method="post">
            <table>
                <tr>
                    <th>Meno:</th>
                    <td><input type="text" name="name" value="<c:out value="${name}"/>"/></td>
                </tr>
                <tr>
                    <th>Telefon:</th>
                    <td><input type="text" name="phoneNumber" value="<c:out value="${phoneNumber}"/>"/></td>
                </tr>
                <tr>
                    <th>Adresa:</th>
                    <td><input type="text" name="address" value="<c:out value="${address}"/>"/></td>
                </tr>
                <input type="hidden" name="id" value="${param.id}"/>
            </table>
            <input type="Submit" value="Odoslat" />
        </form>
        <a href="${pageContext.request.contextPath}/customers/"><button>Zrusit</button></a>
    </c:when>


    <c:otherwise>
        <h2>Novy zakaznik</h2>
        <form action="${pageContext.request.contextPath}/customers/add" method="post">
            <table>
                <tr>
                    <th>Meno:</th>
                    <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
                </tr>
                <tr>
                    <th>Telefon:</th>
                    <td><input type="text" name="phoneNumber" value="<c:out value='${param.phoneNumber}'/>"/></td>
                </tr>
                <tr>
                    <th>Adresa:</th>
                    <td><input type="text" name="address" value="<c:out value='${param.address}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Zadat" />
        </form>
    </c:otherwise>

</c:choose>
</body>
</html>
