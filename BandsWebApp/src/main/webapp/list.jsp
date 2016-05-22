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
                <li class="active"><a href="${pageContext.request.contextPath}/bands/">Bands</a></li>
                <li><a href="${pageContext.request.contextPath}/customers/">Customers</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container theme-showcase" role="main">
    <div class="row">
        <div class="col-md-8">
            <h2>Filter <a href="#filters" class="filtersButton"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a></h2>
            <div style="display: none" class="filterWindow">
                <form action="${pageContext.request.contextPath}/bands/filter" method="post">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="filterName">Name:</label>
                                <input type="text" name="name" id="filterName" value="" class="form-control" placeholder="Name">
                            </div>
                            <div class="form-group">
                                <label for="filterStyles">Styles:</label>
                                <select id="filterStyles" multiple class="form-control" name="styles">
                                    <c:forEach items="${styles}" var="style">
                                        <option>${style.name()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="filterRegion">Region:</label>
                                <select id="filterRegion" class="form-control" name="region">
                                    <option>-</option>
                                    <c:forEach items="${regions}" var="region">
                                        <option>${region.name()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="filterRate">Minimal rate:</label>
                                <input type="text" name="rate" id="filterRate" value="" class="form-control" placeholder="Minimal rate">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label for="filterPricePerHourFrom">Price per hour from:</label>
                                <input type="text" name="pricePerHourFrom" id="filterPricePerHourFrom" value="" class="form-control" placeholder="Price per hour from">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label for="filterPricePerHourTo">Price per hour to:</label>
                                <input type="text" name="pricePerHourTo" id="filterPricePerHourTo" value="" class="form-control" placeholder="Price per hour to">
                            </div>
                        </div>
                    </div>

                    <input type="Submit" class="btn btn-default" value="Filter"/>
                </form>
            </div>
            <h2>Bands list</h2>
            <table class="table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Styles</th>
                    <th>Region</th>
                    <th>Price per hour</th>
                    <th>Rate</th>
                </tr>
                </thead>
                <c:forEach items="${bands}" var="band">
                    <tr>
                        <td align="left"><c:out value="${band.name}"/></td>
                        <td align="center"><c:out value="${band.styles.toString()}"/></td>
                        <td align="center"><c:out value="${band.region}"/></td>
                        <td align="center"><c:out value="${band.pricePerHour}"/></td>
                        <td align="center"><c:out value="${band.rate}"/></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/bands/edit?id=${band.id}"><input type="submit" value="Edit" class="btn btn-info"></form></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/bands/delete?id=${band.id}"><input type="submit" value="Remove" class="btn btn-danger"></form></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="col-md-4">

            <c:if test="${not empty editBand}">
                <h2>Edit band</h2>
            </c:if>
            <c:if test="${empty editBand}">
                <h2>New band</h2>
            </c:if>
            <c:if test="${not empty chyba}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Error:</span>
                    <c:out value="${chyba}"/>
                </div>
            </c:if>

            <c:if test="${not empty editBand}">
                <form action="${pageContext.request.contextPath}/bands/update" method="post" id="editForm">
                    <input type="hidden" name="id" value="${editBand.id}">
                    <div class="form-group">
                        <label for="uinputName">Name:</label>
                        <input type="text" name="name" id="uinputName" value="<c:out value="${editBand.name}"/>" class="form-control" placeholder="Name">
                    </div>
                    <div class="form-group">
                        <label for="uinputStyles">Styles:</label>
                        <select id="uinputStyles" multiple class="form-control" name="styles">
                            <c:forEach items="${styles}" var="style">
                                <option <c:if test="${editBand.styles.contains(style)}">selected</c:if>>${style.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="uinputRegion">Region:</label>
                        <select id="uinputRegion" class="form-control" name="region">
                            ${editBand.region}
                            <c:forEach items="${regions}" var="region">
                                <option <c:if test="${editBand.region == region}">selected</c:if>>${region.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="uinputPricePerHour">Price per hour:</label>
                        <input type="text" name="pricePerHour" id="uinputPricePerHour" value="<c:out value="${editBand.pricePerHour}"/>" class="form-control" placeholder="Price per hour">
                    </div>
                    <div class="form-group">
                        <label for="uinputRate">Rate:</label>
                        <input type="text" name="rate" id="uinputRate" value="<c:out value="${editBand.rate}"/>" class="form-control" placeholder="Rate">
                    </div>

                    <input type="Submit" class="btn btn-success" value="Save" />
                    <a href="${pageContext.request.contextPath}/bands/" class="btn btn-warning">Cancel</a>
                </form>
            </c:if>

            <c:if test="${empty editBand}">
                <form action="${pageContext.request.contextPath}/bands/add" method="post" id="editForm">
                    <div class="form-group">
                        <label for="inputName">Name:</label>
                        <input type="text" name="name" id="inputName" value="<c:out value="${param.name}"/>" class="form-control" placeholder="Name">
                    </div>
                    <div class="form-group">
                        <label for="inputStyles">Styles:</label>
                        <select id="inputStyles" multiple class="form-control" name="styles">
                            <c:forEach items="${styles}" var="style">
                                <option>${style.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="inputRegion">Region:</label>
                        <select id="inputRegion" class="form-control" name="region">
                            <c:forEach items="${regions}" var="region">
                                <option>${region.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="inputPricePerHour">Price per hour:</label>
                        <input type="text" name="pricePerHour" id="inputPricePerHour" value="<c:out value="${param.pricePerHour}"/>" class="form-control" placeholder="Price per hour">
                    </div>
                    <div class="form-group">
                        <label for="inputRate">Rate:</label>
                        <input type="text" name="rate" id="inputRate" value="<c:out value="${param.rate}"/>" class="form-control" placeholder="Rate">
                    </div>

                    <input type="Submit" class="btn btn-success" value="Create" />
                </form>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>
