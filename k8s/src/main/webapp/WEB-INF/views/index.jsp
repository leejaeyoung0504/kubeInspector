<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Redirecting to Login</title>
    <script type="text/javascript">
        window.location.href = "<c:url value='/login'/>";
    </script>
</head>
<body>
    Redirecting to login page...
</body>
</html>