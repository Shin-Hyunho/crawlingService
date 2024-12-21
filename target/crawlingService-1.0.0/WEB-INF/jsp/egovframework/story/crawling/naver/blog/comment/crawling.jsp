<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<%
    /*
     * @File Name : crawling.jsp
     * @Description : 네이버 블로그 댓글 크롤링
     * @  수정일      수정자            수정내용
     * @ -------        --------    ---------------------------
     * @ 2022-03-10		신현호		최초생성 및 PG작업
     * @ ------------------------------------------------------
     */
%> 
<script>
	function crawlingProcModal(callback) {
		$('#crawlingProcModal').modal({
			show:true,
            backdrop:true,
            keyboard:false
		});
		$('#crawlingProcModalClose').unbind().bind('click', function(){
			$('#crawlingProcModal').modal('hide');
			if (callback) callback(false);
		});
		$('#crawlingProcModalSubmit').unbind().bind('click', function(){
			$('#crawlingProcModal').modal('hide');
			if (callback) callback(true);
		});
	}

	function fn_xlsDown() {
		var f = document.getElementById('formEl');
	    f.action = './naverBlogCommentExcelDown.do';
	    f.submit();
	}		
	
	function validator(f) {
		if (!f.targetUrl.value) {
	    	HHCommon.Alert("네이버 블로그 수집 대상 URL을 입력해주세요.", f.targetUrl);
	    	return false;
		}
		if ($('input[name="blogIdOverlap"]:checked').length == 0) {
	    	HHCommon.Alert("블로그 아이디 중복 표시 여부를 선택하십시오.");
	    	return false;
		}
		if ($('input[name="debug"]:checked').length == 0) {
	    	HHCommon.Alert("디버그 사용여부를 선택하십시오.");
	    	return false;
		}
		
		crawlingProcModal(function(confirm) {
			if (confirm) {
				$.ajax({
					type: 'POST',  
					url: './crawlingProc.do',
					data: {targetUrl: f.targetUrl.value, debug: $('input[name="debug"]:checked').val(), commentSearchText: $('#commentSearchText').val(), blogIdOverlap: $('#blogIdOverlap').val(), nId: $('#nId').val(), nPw: $('#nPw').val()},
					dataType: 'text',
					beforeSend: function() {
						$('#resultConsole').html('');
						
						HoldOn.open({ 
					    	theme : 'sk-folding-cube', 
					    	message : '<div id="holdonsMsg" style="padding-top:7px;">[PROCESS_INFO] 크롤링 작업을 시작합니다.</div>'
					    });

						// 진행상황 메시지 처리
				    	reulstConsoleThread = setInterval(function() {
				    		resultConsoleMsg();
				    	}, 700);
					},   
					complete: function() { },
					error: function() {
						HHCommon.Alert('처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
						HoldOn.close();
					}
				}).done(function(data) {
					$('#holdonsMsg').html('${sessionScope.RESULT_CONSOLE_MSG}');
					$('#resultConsole').append('<div>${sessionScope.RESULT_CONSOLE_MSG}</div>');
					$('#resultConsole').scrollTop($('#resultConsole')[0].scrollHeight);
					
					fn_xlsDown();
					HoldOn.close();
				});
			}
		});
		return false;
	}
	
	var prevMsg = '';
	var reulstConsoleThread = null;
	function resultConsoleMsg() {
		$.ajax({
			type: 'POST',  
			url: './resultConsoleMsg.do',
			dataType: 'text',
			beforeSend: function() { },   
			complete: function() { },
			error: function() { }
		}).done(function(data) {
			var result = $.parseJSON(data);
			
			// 이전 메시지와 현재 메시지가 동일하면 콘솔에 미표시
			if (prevMsg != result.msg) {
				$('#holdonsMsg').html(result.msg);
				$('#resultConsole').append('<div>' + result.msg +  '</div>');
				$('#resultConsole').scrollTop($('#resultConsole')[0].scrollHeight);
			}
			
			if (result.flag == 'end') {
				clearInterval(reulstConsoleThread);
			}
			else if (result.flag == 'error') {
				clearInterval(reulstConsoleThread);
			}
			else {
				prevMsg = result.msg;
			}
		});
	}
	
    $(function() {
    	if (${systemProp != 'true'}) $('#crawlingInfoModal').modal('show');
    });     
	
</script>
<form id="formEl" name="formEl" action="./crawlingProc.do" method="post">
	<div class="contents">
		<div class="page-header">
			<h2><span class="glyphicon glyphicon-info-sign"></span> 네이버 블로그 댓글 수집</h2>
			<p class="bg-warning" style="padding:15px;">
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 현재 구동 가능 드라이버 <strong class="text-primary"> (Ver. 99.0.4844.??)</strong>
				<%-- <a href="https://chromedriver.storage.googleapis.com/index.html" target="_blank" title="새창"><small><i class="glyphicon glyphicon-link"></i> 크롬드라이버 다운로드</small></a> --%></span>
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 해당 크롤링 서비스를 이용하려면 사용자 PC에 설치된 크롬의 버전이 위 구동 가능 드라이버 버전과 동일한 버전이 설치되어야 합니다.</span>
			</p>
			<div style="text-align:right;">
				<small><span class="glyphicon glyphicon-ok red-color"></span>는 필수 입력사항입니다.</small>
			</div>
		</div>
		<div class="panel panel-default">
  			<div class="panel-body">
				<div class="table-responsive">
					<table class="table table-striped">
						<caption class="hidden">네이버 블로그 댓글 수집</caption>
						<colgroup>
							<col style="width:20%;" />
							<col style="width:auto;" />
						</colgroup>
						<tbody>
					        <tr>
					            <th scope="row" class="primary-line-left">네이버 로그인 정보 <span class="glyphicon glyphicon-ok red-color"></span></th>
					            <td class="form-inline">
					            	<label for="nId">아이디 : </label><input type="text" id="nId" name="nId" value="knoc3" class="form-control" placeholder="아이디" />
					            	<label for="nPw">비밀번호 : </label><input type="password" id="nPw" name="nPw" value="knocpr1!" class="form-control" placeholder="비밀번호" />
					            </td>
					        </tr>
					        <tr>
					            <th scope="row" class="primary-line-left"><label for="targetUrl">네이버 블로그 수집 대상 URL</label> <span class="glyphicon glyphicon-ok red-color"></span></th>
					            <td><input type="text" id="targetUrl" name="targetUrl" value="https://m.blog.naver.com/PostView.naver?blogId=knoc3&logNo=222672041208&navType=by" class="form-control" placeholder="https://m.blog.naver.com/PostView.naver?blogId=knoc3&logNo=222597826407&navType=by" />
					            	<span class="help-block"><small>ex) 네이버 블로그 게시물의 URL을 입력해주시기 바랍니다.</small></span>
					            </td>
					        </tr>
					        <tr>
					            <th scope="row" class="primary-line-left"><label for=commentSearchText>댓글 내용 색인 단어 (정답)</label></th>
					            <td><input type="text" id="commentSearchText" name="commentSearchText" class="form-control" placeholder="색인을 원하시는 단어를 입력하십시오." />
					            	<span class="help-block"><small>ex) 수집되는 댓글 내용 중 해당 텍스트가 존재 할 경우 엑셀 다운로드 시 색상으로 표시됩니다. 모든 문자열은 공백을 제거하며, 영문일 경우 소문자로 변경하여 검색합니다.</small></span>
					            </td>
					        </tr>
					        <tr>
					            <th scope="row">블로그 아이디 중복 확인 <span class="glyphicon glyphicon-ok red-color"></span></th>
					            <td>
					                <label for="blogIdOverlap01" class="radio-inline"><input type="radio" id="blogIdOverlap01" name="blogIdOverlap" value="Y" checked="checked" /><span class="label label-success">예</span></label> 
					                <label for="blogIdOverlap02" class="radio-inline"><input type="radio" id="blogIdOverlap02" name="blogIdOverlap" value="N" /><span class="label label-danger">아니요</span></label> 
					                <span class="help-block"><small>ex) 블로그 아이디가 중복된 사용자는 엑셀파일에 색상으로 표시됩니다.</small></span>
					            </td>
					        </tr>
					        <tr>
					            <th scope="row">크롤링 작업 진행 DEBUG <span class="glyphicon glyphicon-ok red-color"></span></th>
					            <td>
					                <label for="debugY" class="radio-inline"><input type="radio" id="debugY" name="debug" value="DEBUG" checked="checked" /><span class="label label-success">예 (진행되는 작업 모두를 직접 확인)</span></label> 
					                <label for="debugN" class="radio-inline"><input type="radio" id="debugN" name="debug" value="RUN" /><span class="label label-danger">아니요 (진행되는 작업 모두가 백그라운드에서 진행)</span></label> 
					            </td>
					        </tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
  		<div id="buttonFieldSet" style="text-align:center;">
  			<button type="button" onclick="validator(document.getElementById('formEl'));" class="btn btn-info btn-lg">
                <span><i class="glyphicon glyphicon-ok-circle"></i> 실행</span>
			</button>
		</div>
		
		<div style="margin-top:20px;">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-list-alt"></span> Console <small>현재 처리중인 크롤링 처리 결과를 표시합니다.</small></h3>
				</div>
				<div class="panel-body panel-scroll" id="resultConsole" style="height:350px;overflow-y:auto;font-size:12px;"></div>
			</div>
		</div>
	</div>
</form>
	
<div class="modal fade" id="crawlingInfoModal" tabindex="-1" role="dialog" aria-labelledby="crawlingInfoModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title"><i class="glyphicon glyphicon-exclamation-sign"></i> 알림</h4>
			</div>
			<div class="modal-body" id="crawlingInfoModalBody">
				<p class="bg-danger" style="padding:15px;">
					<span class="help-block">크롬드라이버가 설치되지 않았습니다.<br />해당 프로그램을 이용전 아래의 링크를 통해 사용하시는 크롬의 버전과 동일한 드라이버를 다운 후 설치해주시기 바랍니다.<br />
					<a href="https://chromedriver.storage.googleapis.com/index.html" target="_blank" title="새창"><small>크롬드라이버 다운로드</small></a>
					</span>
				</p>
			</div>
		</div>
	</div>
</div> 
		
<div class="modal fade" id="crawlingProcModal" tabindex="-1" role="dialog" aria-labelledby="crawlingProcModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 알림</h4>
			</div>
			<div class="modal-body">
				<p><strong style="color:#FF3C44;">크롤링 작업 진행 DEBUG 여부를 "예" 선택시</strong> <span style="color:#FF3C44;">아래와 같이 작업이 진행되는 동안 강제로 종료하지 마십시오.</span></p>
				<p>
					<span style="color:#2F70A9;">실행 -> 네이버 로그인 페이지 이동 -> 입력한 로그인 정보로 네이버 로그인 -> 수집 대상 URL로 페이지 이동 -> 댓글 목록 이동 -> 댓글이 전체가 오픈될때까지 자동 스크롤 진행 -> 
					전체 댓글 수집 -> 색인 단어 및 아이디 중복 확인 -> 수집된 데이터 엑셀 변환 -> 엑셀 파일 다운로드<br /><br />
					브라우저를 닫으면 작업이 종료되므로 실행 후 수집이 완료될때까지 아무런 작업을 하지 말고 기다려주십시오.
				</p>
				<p>수집을 시작하시겠습니까?</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal" id="crawlingProcModalClose">닫기</button>
				<button type="button" class="btn btn-primary" id="crawlingProcModalSubmit" data-loading-text="수집중..." autocomplete="off">확인</button>
			</div>
		</div>
	</div>
</div>