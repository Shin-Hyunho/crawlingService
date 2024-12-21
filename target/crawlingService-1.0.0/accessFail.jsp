<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<%
    /*
     * @File Name : error.jsp
     * @Description : 에러페이지
     * @  수정일      수정자            수정내용
     * @ -------        --------    ---------------------------
     * @ 2018-12-02		신현호		최초생성 및 PG작업
     * @ ------------------------------------------------------
     */
%> 
<c:set var="CONTEXTPATH" value="${pageContext.request.contextPath}"/>
<%@ page isErrorPage="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ko" lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>일시적인 서비스 장애 안내.</title>
	<link href="<c:url value='/css/egovframework/story/base.css' />" rel="stylesheet" type="text/css" />
	<style>
	#error {padding-top:260px; text-align:center; background:url(/images/error_img.gif) 50% 30% no-repeat;}
	#error p.title {font-size:25px; color:#464646; line-height:30px; }
	#error .message {margin:0px auto; padding:10px 0 30px; font-size:14px;}
	#error a.btn {display:inline-block; vertical-align: middle; height:36px; padding:0 30px; margin-left:4px; font-size:13px; font-weight:bold; color:#ffffff !important; line-height:36px; text-decoration:none !important; border-radius:3px; background:#006cd9;}
	</style>
	<script>
	function fncGoAfterErrorPage(){
    	history.back(-2);
	}
	</script>
</head>
<body>
<div id="error">
	<p class="title">관리자 제어정책에 해당하는 접근자가 아닙니다.</p>
	<p class="message">
		관리자 페이지에 접근 권한이 없습니다. [IP : <strong><%=request.getRemoteAddr()%></strong>]
	</p>
	<a href="javascript:fncGoAfterErrorPage();" class="btn">이전 페이지로</a>
	<!-- 디버깅용 - 필요시 주석해제 -->
	<%-- <ul style="margin-top:20px;">
		<li><strong>Timestamp:</strong> <fmt:formatDate value="${date}" type="both" dateStyle="long" timeStyle="long" /></li>
		<li><strong>Action:</strong> <c:out value="${requestScope['javax.servlet.forward.request_uri']}" /></li>
		<li><strong>Exception:</strong> <c:out value="${requestScope['javax.servlet.error.exception']}" /></li>
		<li><strong>Message:</strong> <c:out value="${requestScope['javax.servlet.error.message']}" /></li>
		<li><strong>Status code:</strong> <c:out value="${requestScope['javax.servlet.error.status_code']}" /></li>
		<li><strong>User agent:</strong> <c:out value="${header['user-agent']}" /></li>
	</ul> --%>
</div>
</body>
</html>