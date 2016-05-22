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
    <script src="${pageContext.request.contextPath}/script.js"></script>
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
            <h2>Customer list</h2>
            <table class="table">
                <thead>
                <tr>
                    <th align="center">Name</th>
                    <th align="center">Phone number</th>
                    <th align="center">Address</th>
                </tr>
                </thead>
                <c:forEach items="${customers}" var="customer">
                    <tr>
                        <td align="left"><c:out value="${customer.name}"/></td>
                        <td align="center"><c:out value="${customer.phoneNumber}"/></td>
                        <td align="center"><c:out value="${customer.address}"/></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/customers/edit?id=${customer.id}"><input type="submit" value="Edit" class="btn btn-info"></form></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/customers/delete?id=${customer.id}"><input type="submit" value="Remove" class="btn btn-danger"></form></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="col-md-4">
            <c:if test="${not empty editCustomer}">
                <h2>Edit customer</h2>
            </c:if>
            <c:if test="${empty editCustomer}">
                <h2>New customer</h2>
            </c:if>
            <c:if test="${not empty chyba}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Error:</span>
                    <c:out value="${chyba}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty editCustomer}">
                    <form action="${pageContext.request.contextPath}/customers/update" method="post">
                        <div class="form-group">
                            <label for="inputEditName">Name:</label>
                            <input type="text" name="name" id="inputEditName" value="<c:out value="${editCustomer.name}"/>" class="form-control" placeholder="Name">
                        </div>
                        <div class="form-group">
                            <label for="inputEditPhoneNumber">Phone number:</label>
                            <input type="text" name="phoneNumber" id="inputEditPhoneNumber" value="<c:out value="${editCustomer.phoneNumber}"/>" class="form-control" placeholder="Phone number">
                        </div>
                        <div class="form-group">
                            <label for="inputEditAddress">Address:</label>
                            <input type="text" name="address" id="inputEditAddress" value="<c:out value="${editCustomer.address}"/>" class="form-control" placeholder="Address">
                        </div>
                        <input type="hidden" name="id" value="${editCustomer.id}">

                        <input type="Submit" class="btn btn-success" value="Save" />
                        <a href="${pageContext.request.contextPath}/customers/" class="btn btn-warning">Cancel</a>
                    </form>
                </c:when>


                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/customers/add" method="post">
                        <div class="form-group">
                            <label for="inputName">Name:</label>
                            <input type="text" name="name" id="inputName" value="<c:out value="${param.name}"/>" class="form-control" placeholder="Name">
                        </div>
                        <div class="form-group">
                            <label for="inputPhoneNumber">Phone number:</label>
                            <input type="text" name="phoneNumber" id="inputPhoneNumber" value="<c:out value="${param.phoneNumber}"/>" class="form-control" placeholder="Phone number">
                        </div>
                        <div class="form-group">
                            <label for="inputAddress">Address:</label>
                            <input type="text" name="address" id="inputAddress" value="<c:out value="${param.address}"/>" class="form-control" placeholder="Address">
                        </div>
                        <input type="Submit" class="btn btn-default" value="Create" />
                    </form>
                </c:otherwise>

            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
