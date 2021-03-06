<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=utf-8" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Log in with your account</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">

    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

</head>
<body>
<div class="box">
    <form method="get" action = "${contextPath}/search" >
        <div class="container-1">
            <span class="icon"><i class="fa fa-search"></i></span>
            <input type="search" name = "request" id="search" placeholder="${request}" />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </div>
    </form>
</div>
<form id ="searchs" >
    <table id="keywords" cellspacing="0" cellpadding="0">
        <thead>
        <tr>
            <th><span>Image</span></th>
            <th><span>Price</span></th>
            <th><span>Name</span></th>
        </tr>
        </thead>
        <tbody id = "tbodyid">
        <c:forEach items="${products}" var="product">
            <tr>
                <td class="glyphicon-th-list, text-left, lalign">
                    <a href="${product.url}">
                        <img height="150" weight = "150" src = "${product.image}" alt = "image not found"/>
                    </a>
                </td>
                <td>
                    <c:out value = "${product.price}"/>
                </td>
                <td class="glyphicon-th-list, text-left, lalign"> <c:out value = "${product.name}"/> </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</form>

<script type="text/javascript">
    $(document).ready(function(){
        var contentLoadTriggered = false;
        var tbody = $("#tbodyid");
        window.onscroll = function() {
            if (document.body.scrollTop >= tbody.height() - window.innerHeight && !contentLoadTriggered) {
                contentLoadTriggered = true;
                $.get("getmore", function(data) {
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var dataname = decodeURIComponent(data[i].name).replace(/\+/gi, ' ');
                        var dataprice = decodeURIComponent(data[i].price).replace(/\+/gi, ' ');
                        html += '<tr><td class="glyphicon-th-list, text-left, lalign">' +
                            '   <a href="' + data[i].url + '"><img height="150" weight = "150" src = "' + data[i].image + '" alt = "image not found"/></a>' +
                            '</td><td>' + dataprice + '</td>' +
                            '    <td class="glyphicon-th-list, text-left, lalign">' + dataname + '</td>' +
                            '</tr>';
                    }
                    tbody.innerHTML += html;
                    tbody.html(tbody.html() + html);
                    contentLoadTriggered = false;
                });
            }
        };
    });
</script>

</body>
</html>
