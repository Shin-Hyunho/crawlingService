<%@page import="egovframework.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<%
    /*
     * @File Name : login.jsp
     * @Description : 로그인
     * @  수정일      수정자            수정내용
     * @ -------        --------    ---------------------------
     * @ 2018-12-02		신현호		최초생성 및 PG작업
     * @ ------------------------------------------------------
     */
%>
<script>   
function validator(form) {
	if (!form.userId.value) {
		HHCommon.Alert("아이디를 입력하세요!.", form.userId);
        return false;
	}
	if (!form.userPw.value) {
		HHCommon.Alert("비밀번호을 입력하세요!.", form.userPw);
		return false;
	}
	return true;
}
</script>
<div class="login-nwrapper">
	<div class="login-n-inner">
		<div class="login-formbox">
		 	<div class="content-wrap">
		   		<form action="${pageContext.request.contextPath}/loginProc.do" method="post" name="loginForm" id="loginForm" onsubmit="return validator(this);">
		   			<ul class="login-inputbox">
		   				<li><label for="userId" class="con01"><span class="blind">아이디</span></label><input type="text" id="userId" name="userId" placeholder="아이디를 입력하세요." value="" /></li>
		   				<li><label for="userPw" class="con02"><span class="blind">비밀번호</span></label><input type="password" id="userPw" name="userPw" placeholder="비밀번호를 입력하세요." value="" /></li>
		   			</ul>
		   			<button type="submit" id="sign-in" class="login-nbtn">로그인</button>
		 		</form>
			</div>
		</div>
	</div>
</div>