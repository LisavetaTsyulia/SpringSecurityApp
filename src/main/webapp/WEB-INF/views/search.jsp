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

</head>
<script type="text/javascript">
    $(document).ready(function(){
        $contentLoadTriggered = false;
        $("#content-box").scroll(function(){
            if($("#content-box").scrollTop() >= ($("#content-wrapper").height() - $("#content-box").height()) && $contentLoadTriggered == false)
            {
                $contentLoadTriggered = true;
                $.get("getmore", function(data){
                    $("#searchs").append(data);
                    $contentLoadTriggered = false;
                });
            }

        });
    });
</script>
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
<table class = "table-responsive">
    <form id ="searchs" >
        <c:forEach items="${products}" var="product">
            <tr>
                <th class="glyphicon-th-list, text-left"> <a href=<c:out value = "${product.url}"/>/> <c:out value = "${product.url}"/></th>
            </tr>
            <tr>
                <th class="glyphicon-th-list, text-left">
                    <a href="${product.url}">
                        <img height="150" weight = "150" src = "${product.image}" alt = "sorry"/>
                    </a>
                </th>
            </tr>
            <tr>
                <th class="glyphicon-th-list, text-left"> <c:out value = "${product.name}"/> </th>
            </tr>
        </c:forEach>

    </form>
</table>
<script>
</script>

</body>
</html>
