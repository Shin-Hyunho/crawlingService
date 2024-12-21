<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<meta name="description" content="">
	<meta name="keywords" content="">
	<title></title>
	<link rel="shortcut icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<link rel="icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<script>
	window.onload = function() {
		window.location.href="${pageContext.request.contextPath}/autoCamp/resv/crawling.do";
	}
	</script>
</head>
<body>
	
</body>
</html>