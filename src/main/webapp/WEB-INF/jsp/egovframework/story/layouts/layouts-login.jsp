<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<%
    /*
     * @File Name : layouts-login.jsp
     * @Description : 로그인 레이아웃
     * @  수정일      수정자            수정내용
     * @ -------        --------    ---------------------------
     * @ 2018-12-02		신현호		최초생성 및 PG작업
     * @ ------------------------------------------------------
     */
%> 
<!DOCTYPE html>
<html lang="ko" class="login-nbg">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<meta name="description" content="">
	<meta name="keywords" content="">
  	<meta name="viewport" content="width=device-width,initial-scale=0.6,minimum-scale=0.6,maximum-scale=0.6,user-scalable=no">
	<title>캠핑장 실시간 예약확인 서비스</title>
	<link rel="shortcut icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<link rel="icon" href="<c:url value='/favicon.ico' />" type="image/x-icon" />
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/base.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/bootstrap.min.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/bootstrap-theme.min.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/bootstrap-custom.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/jquery-ui.css' />">
	<link rel="stylesheet" href="<c:url value='/css/egovframework/story/login.css' />">
	<script src="<c:url value='/js/egovframework/story/jquery-1.11.3.min.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/bootstrap.min.js' />"></script>
	<!--[if lt IE 9]>
	<script src="<c:url value='/js/egovframework/story/html5shiv.min.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/respond.min.js' />"></script>
	<![endif]-->
	<script src="<c:url value='/js/egovframework/story/jquery-ui.js' />"></script>
	<script src="<c:url value='/js/egovframework/story/HHCommon.js' />"></script>
</head>
<body class="login-innerbg">
	<noscript> 
		이 사이트의 기능을 모두 활용하기 위해서는 자바스크립트를 활성화 시킬 필요가 있습니다.
		<a href="http://www.enable-javascript.com/ko/" target="_blank" title="새창">
		브라우저에서 자바스크립트를 활성화하는 방법</a>을 참고 하세요.
	</noscript>
	
	<t:insertAttribute name="content"/>

	<!-- Alert Modal -->
	<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><i class="glyphicon glyphicon-exclamation-sign"></i> 알림</h4>
				</div>
				<div class="modal-body" id="alertModalBody"></div>
			</div>
		</div>
	</div> 
	
	<!-- Confirm Modal -->
	<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="confirmModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 알림</h4>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" id="confirmModalClose">닫기</button>
					<button type="button" class="btn btn-primary" id="confirmModalSubmit">확인</button>
				</div>
			</div>
		</div>
	</div> 
</body>
</html>