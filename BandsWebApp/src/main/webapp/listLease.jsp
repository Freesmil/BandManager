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
                <li class="active"><a href="${pageContext.request.contextPath}/leases/">Leases</a></li>
                <li><a href="${pageContext.request.contextPath}/customers/">Customers</a></li>
                <li><a href="${pageContext.request.contextPath}/xml/">Import/Export</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container theme-showcase" role="main">
    <div class="row">
        <div class="col-md-8">
            <h2>Filter <a href="#filters" class="filtersButton"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a></h2>
            <div style="display: none" class="filterWindow">
                <form action="${pageContext.request.contextPath}/leases/filter" method="post">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="filterBand">Band:</label>
                                <select id="filterBand" class="form-control" name="bandId">
                                    <c:forEach items="${bands}" var="band">
                                        <option value="${band.id}">${band.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="filterCustomer">Customer:</label>
                                <select id="filterCustomer" class="form-control" name="customerId">
                                    <c:forEach items="${customers}" var="customer">
                                        <option value="${customer.id}">${customer.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>

                    <input type="Submit" class="btn btn-default" value="Filter"/>
                </form>
            </div>
            <h2>List of leases</h2>
            <table class="table">
                <thead>
                <tr>
                    <th>Customer</th>
                    <th>Band</th>
                    <th>Date</th>
                    <th>Place</th>
                    <th>Duration</th>
                </tr>
                </thead>
                <c:forEach items="${leases}" var="lease">
                    <tr>
                        <td align="left"><c:out value="${lease.customer.name}"/></td>
                        <td align="center"><c:out value="${lease.band.name}"/></td>
                        <td align="center"><c:out value="${lease.date}"/></td>
                        <td align="center"><c:out value="${lease.place}"/></td>
                        <td align="center"><c:out value="${lease.duration}"/></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/leases/edit?id=${lease.id}"><input type="submit" value="Edit" class="btn btn-info"></form></td>
                        <td align="right"><form method="post" action="${pageContext.request.contextPath}/leases/delete?id=${lease.id}"><input type="submit" value="Remove" class="btn btn-danger"></form></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="col-md-4">

            <c:if test="${not empty editLease}">
                <h2>Edit lease</h2>
            </c:if>
            <c:if test="${empty editLease}">
                <h2>New lease</h2>
            </c:if>
            <c:if test="${not empty chyba}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Error:</span>
                    <c:out value="${chyba}"/>
                </div>
            </c:if>

            <c:if test="${not empty editLease}">
                <form action="${pageContext.request.contextPath}/leases/update" method="post" id="editForm">
                    <input type="hidden" name="id" value="${editLease.id}">
                    <div class="form-group">
                        <label for="uinputCustomer">Customer:</label>
                        <select id="uinputCustomer" class="form-control" name="customerId">
                            <c:forEach items="${customers}" var="customer">
                                <option value="${customer.id}" <c:if test="${editLease.customer.id == customer.id}">selected</c:if>>${customer.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="uinputBand">Band:</label>
                        <select id="uinputBand" class="form-control" name="bandId">
                            <c:forEach items="${bands}" var="band">
                                <option value="${band.id}" <c:if test="${editLease.band.id == band.id}">selected</c:if>>${band.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="uinputDate">Date:</label>
                        <input type="date" name="date" id="uinputDate" value="<c:out value="${editLease.date}"/>" class="form-control" placeholder="Date">
                    </div>
                    <div class="form-group">
                        <label for="uinputRegion">Place:</label>
                        <select id="uinputRegion" class="form-control" name="place">
                            <c:forEach items="${regions}" var="region">
                                <option <c:if test="${editLease.place == region}">selected</c:if>>${region.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="uinputDuration">Duration:</label>
                        <input type="text" name="duration" id="uinputDuration" value="<c:out value="${editLease.duration}"/>" class="form-control" placeholder="Duration">
                    </div>

                    <input type="Submit" class="btn btn-success" value="Save" />
                    <a href="${pageContext.request.contextPath}/leases/" class="btn btn-warning">Cancel</a>
                </form>
            </c:if>

            <c:if test="${empty editLease}">
                <form action="${pageContext.request.contextPath}/leases/add" method="post" id="editForm">
                    <div class="form-group">
                        <label for="inputCustomer">Customer:</label>
                        <select id="inputCustomer" class="form-control" name="customerId">
                            <c:forEach items="${customers}" var="customer">
                                <option value="${customer.id}">${customer.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="inputBand">Band:</label>
                        <select id="inputBand" class="form-control" name="bandId">
                            <c:forEach items="${bands}" var="band">
                                <option value="${band.id}">${band.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="inputDate">Date:</label>
                        <input type="date" name="date" id="inputDate" class="form-control" placeholder="Date">
                    </div>
                    <div class="form-group">
                        <label for="inputRegion">Place:</label>
                        <select id="inputRegion" class="form-control" name="place">
                            <c:forEach items="${regions}" var="region">
                                <option >${region.name()}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="inputDuration">Duration:</label>
                        <input type="text" name="duration" id="inputDuration" class="form-control" placeholder="Duration">
                    </div>

                    <input type="Submit" class="btn btn-success" value="Create" />
                </form>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>
