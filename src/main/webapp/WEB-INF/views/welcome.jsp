<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Welcome</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%--
<div class="container">

    <c:if test="${pageContext.request.userPrincipal.name != null}">
        <form id="logoutForm" method="POST" action="${contextPath}/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <h2>Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a>
        </h2>

        <table class = "table_price">

            <col width="400px" />
            <col width="70px" />
            <col width="70px" />
        <c:forEach items="${userList}" var="someUser">
            <c:if test="${pageContext.request.userPrincipal.name != someUser.username}">
                <tr class="th">
                   <th class="th, text-center"> <c:out value = "${someUser.username}"/> </th>

                   <th class="th, text-center">
                       <form action = "/welcome2/${someUser.id}">
                       <button class="my_button custom" type = "submit" name="button" value="delete">Delete</button>
                       </form>
                   </th>
                   <th class="th, text-center">
                       <form action = "/welcome2/${someUser.id}">
                               <button class="my_button custom" type="submit" name="button" value="block">
                                   <c:if test="${someUser.status == 'ACTIVE'}">
                                       Block
                                   </c:if>
                                   <c:if test="${someUser.status == 'BLOCKED'}">
                                       Unlock
                                   </c:if>
                               </button>
                       </form>
                   </th>
                </tr>
            </c:if>
        </c:forEach>
    </table>

</c:if>
</div>
--%>
<div class="container">
    <c:if test="${pageContext.request.userPrincipal.name != null}">
    <form action="/user-list/change">
        <button class="my_button custom" type = "submit" name="button" value="deleteButton">
            Delete
        </button>
        <button class="my_button custom" type="submit" name="button" value="blockButton">
            Block/Unblock
        </button>
        <table class = "table_price">
            <c:forEach items="${userList}" var="user">
                <c:if test="${pageContext.request.userPrincipal.name != user.username}">
                    <tr class="th">
                        <td class="th, text-left"><c:out value = "${user.username}"/> </td>
                        <td class="th, text-center">
                            <c:choose>
                                <c:when test="${user.status.equals('BLOCKED')}">
                                    Blocked
                                </c:when>
                                <c:otherwise>
                                    Unblocked
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="th, text-center">
                            <input type="checkbox" name="personId" value="${user.id}"/>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
        <form/>
        </c:if>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
<%--
<script>
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8087/redirect", true);
    xhr.send("old");
    if (xhr.status !== 200) {
        alert( xhr.status + ': ' + xhr.statusText );
    } else {
        alert( xhr.responseText );
    }
</script>
--%>
</body>
</html>