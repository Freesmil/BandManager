<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bands project</title>

    <link href="${pageContext.request.contextPath}/style.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
</head>
<body style="padding-top: 70px; padding-bottom: 30px;">

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Bands project</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/bands/">Bands</a></li>
                <li><a href="${pageContext.request.contextPath}/customers/">Customers</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container theme-showcase" role="main">
    <div class="row">
        <div class="col-md-8">
            <table class="table">
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
                        <td align="left"><form method="post" action="${pageContext.request.contextPath}/bands/update?id=${band.id}"><input type="submit" value="Upravit"></form></td>
                        <td align="left"><form method="post" action="${pageContext.request.contextPath}/bands/delete?id=${band.id}"><input type="submit" value="Smazat"></form></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="col-md-4">
            <h2>Zadejte kapelu</h2>
            <c:if test="${not empty chyba}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Chyba:</span>
                    <c:out value="${chyba}"/>
                </div>
            </c:if>
            <form action="${pageContext.request.contextPath}/bands/add" method="post">
                <div class="form-group">
                    <label for="inputName">Nazev kapely:</label>
                    <input type="text" name="name" id="inputName" value="<c:out value="${param.name}"/>" class="form-control" placeholder="Meno">
                </div>
                <div class="form-group">
                    <label for="inputStyles">Styly:</label>
                    <select id="inputStyles" multiple class="form-control" name="styles">
                        <option>blues</option>
                        <option>classical</option>
                        <option>country</option>
                        <option>disco</option>
                        <option>dnb</option>
                        <option>dubstep</option>
                        <option>electronic</option>
                        <option>folk</option>
                        <option>funk</option>
                        <option>hipHop</option>
                        <option>house</option>
                        <option>jazz</option>
                        <option>metal</option>
                        <option>pop</option>
                        <option>punk</option>
                        <option>reggae</option>
                        <option>rock</option>
                        <option>rnb</option>
                        <option>ska</option>
                        <option>techno</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="inputRegion">Region:</label>
                    <select id="inputRegion" class="form-control" name="region">
                        <option>jihocesky</option>
                        <option>jihomoravsky</option>
                        <option>karlovarsky</option>
                        <option>kralovohradecky</option>
                        <option>liberecky</option>
                        <option>moravskosliezsky</option>
                        <option>olomoucky</option>
                        <option>pardubicky</option>
                        <option>plzensky</option>
                        <option>praha</option>
                        <option>stredocesky</option>
                        <option>ustecky</option>
                        <option>vysocina</option>
                        <option>zlinsky</option>
                        <option>slovensko</option>
                        <option>zahranici</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="inputPricePerHour">Cena za hodinu:</label>
                    <input type="text" name="pricePerHour" id="inputPricePerHour" value="<c:out value="${param.pricePerHour}"/>" class="form-control" placeholder="Cena za hodinu">
                </div>
                <div class="form-group">
                    <label for="inputRate">Rate:</label>
                    <input type="text" name="rate" id="inputRate" value="<c:out value="${param.rate}"/>" class="form-control" placeholder="Rate">
                </div>
                <input type="Submit" class="btn btn-default" value="Zadat" />
            </form>
        </div>
    </div>
</div>

</body>
</html>
