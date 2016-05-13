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
                <li><a href="${pageContext.request.contextPath}/bands/">Bands</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/customers/">Customers</a></li>
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
                    <th align="center">Meno</th>
                    <th align="center">Telefon</th>
                    <th align="center">Adresa</th>
                </tr>
                </thead>
                <c:forEach items="${customers}" var="customer">
                    <tr>
                        <td align="center"><c:out value="${customer.name}"/></td>
                        <td align="center"><c:out value="${customer.phoneNumber}"/></td>
                        <td align="center"><c:out value="${customer.address}"/></td>
                        <td><form method="post" action="${pageContext.request.contextPath}/customers/showUpdate?id=${customer.id}"><input type="submit" value="Upravit"></form></td>
                        <td><form method="post" action="${pageContext.request.contextPath}/customers/delete?id=${customer.id}"><input type="submit" value="Smazat"></form></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="col-md-4">
            <c:if test="${not empty chyba}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Chyba:</span>
                    <c:out value="${chyba}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty param.id}">
                    <h2>Upravit</h2>
                    <form action="${pageContext.request.contextPath}/customers/update" method="post">
                        <div class="form-group">
                            <label for="inputEditName">Meno:</label>
                            <input type="text" name="name" id="inputEditName" value="<c:out value="${name}"/>" class="form-control" placeholder="Meno">
                        </div>
                        <div class="form-group">
                            <label for="inputEditPhoneNumber">Telefon:</label>
                            <input type="text" name="phoneNumber" id="inputEditPhoneNumber" value="<c:out value="${phoneNumber}"/>" class="form-control" placeholder="Telefon">
                        </div>
                        <div class="form-group">
                            <label for="inputEditAddress">Adresa:</label>
                            <input type="text" name="address" id="inputEditAddress" value="<c:out value="${address}"/>" class="form-control" placeholder="Adresa">
                        </div>
                        <input type="hidden" name="id" value="${param.id}"/>

                        <input type="Submit" class="btn btn-default" value="Odoslat" />
                    </form>
                    <a href="${pageContext.request.contextPath}/customers/"><button>Zrusit</button></a>
                </c:when>


                <c:otherwise>
                    <h2>Novy zakaznik</h2>
                    <form action="${pageContext.request.contextPath}/customers/add" method="post">
                        <div class="form-group">
                            <label for="inputName">Meno:</label>
                            <input type="text" name="name" id="inputName" value="<c:out value="${param.name}"/>" class="form-control" placeholder="Meno">
                        </div>
                        <div class="form-group">
                            <label for="inputPhoneNumber">Telefon:</label>
                            <input type="text" name="phoneNumber" id="inputPhoneNumber" value="<c:out value="${param.phoneNumber}"/>" class="form-control" placeholder="Telefon">
                        </div>
                        <div class="form-group">
                            <label for="inputAddress">Adresa:</label>
                            <input type="text" name="address" id="inputAddress" value="<c:out value="${param.address}"/>" class="form-control" placeholder="Adresa">
                        </div>
                        <input type="Submit" class="btn btn-default" value="Zadat" />
                    </form>
                </c:otherwise>

            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
