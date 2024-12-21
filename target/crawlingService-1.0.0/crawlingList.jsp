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
</head>
<body>
	<div id="container">
		<div id="contentarea">
			<div class="contents">
				<div class="page-header">
					<h2><span class="glyphicon glyphicon-info-sign"></span> 크롤링 서비스 선택</h2>
				</div>
				<%-- <div class="panel panel-default">
		  			<div class="panel-body">
		  				<ul class="list-group">
							<li class="list-group-item"><a href="${pageContext.request.contextPath}/naver/blog/comment/crawling.do""><i class="glyphicon glyphicon-link"></i> 네이버 블로그 댓글 크롤링</a></li>
							<li class="list-group-item"><a href="${pageContext.request.contextPath}/autoCamp/resv/crawling.do""><i class="glyphicon glyphicon-link"></i> 울산 지자체 캠핑장 크롤링</a></li>
						</ul>
		  			</div>
				</div> --%>
				
				<div class="list-group">
				  	<a href="${pageContext.request.contextPath}/naver/blog/comment/crawling.do" class="list-group-item">
				    	<h4 class="list-group-item-heading"><i class="glyphicon glyphicon-link"></i> 네이버 블로그 댓글</h4>
				    	<p class="list-group-item-text"><small>네이버 블로그에 작성된 글의 댓글을 수집하여 입력한 텍스트를 검색하여 엑셀로 변환 추출 서비스를 제공</small></p>
				  	</a>
				  	<a href="${pageContext.request.contextPath}/autoCamp/resv/crawling.do" class="list-group-item">
				    	<h4 class="list-group-item-heading"><i class="glyphicon glyphicon-link"></i> 울산 지자체 캠핑장</h4>
				    	<p class="list-group-item-text"><small>울산 지자체 캠핑장의 예약 상태를 확인하고 자동예약까지 지원하는 서비스를 제공</small></p>
				  	</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>