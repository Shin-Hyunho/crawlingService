<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
  	<meta name="viewport" content="width=device-width,initial-scale=0.6,minimum-scale=0.6,maximum-scale=0.6,user-scalable=no">
	<meta name="description" content="">
	<meta name="keywords" content="">
	<title>크롤링 서비스</title>
	<link rel="shortcut icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<link rel="icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/base.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/bootstrap.min.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/bootstrap-theme.min.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/jquery-ui.css' />">
	<script src="<c:url value='/js/egovframework/story/jquery-1.11.3.min.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/jquery-ui.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/bootstrap.min.js' />"></script>
	<!--[if lt IE 9]>
	<script src="<c:url value='/js/egovframework/story/html5shiv.min.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/respond.min.js' />"></script>
	<![endif]-->
	<%-- <script>
	window.onload = function() {
		window.location.href="${pageContext.request.contextPath}/autoCamp/resv/crawling.do";
	}
	</script> --%>
</head>
<body>
<%
if (request.getSession().getAttribute("IS_LOGIN") != null) { 
%>
<div id="container">
	<div id="contentarea">
		<div class="contents">
			<div class="page-header">
				<h2><span class="glyphicon glyphicon-info-sign"></span> 크롤링 서비스 선택</h2>
			</div>
			<div class="panel panel-default">
	  			<div class="panel-body">
	  				<ul class="list-group">
						<%-- <li class="list-group-item"><a href="${pageContext.request.contextPath}/naver/blog/comment/crawling.do""><i class="glyphicon glyphicon-link"></i> 네이버 블로그 댓글 크롤링</a></li> --%>
						<li class="list-group-item"><a href="#" onclick="alert('접근 권한이 없습니다!. 관리자에게 문의하십시오!.');"><i class="glyphicon glyphicon-link"></i> 네이버 블로그 댓글 크롤링</a></li>
						<li class="list-group-item"><a href="${pageContext.request.contextPath}/autoCamp/resv/crawling.do""><i class="glyphicon glyphicon-link"></i> 울산 지자체 캠핑장 크롤링</a></li>
					</ul>
	  			</div>
			</div>
		</div>
	</div>
</div>
<%	
} else {
	response.sendRedirect(request.getContextPath() + "/login.do");
}
%>
</body>
</html>