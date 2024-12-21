<%@page import="egovframework.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/story/common/taglib.jsp" %>
<%
    /*
     * @File Name : crawling.jsp
     * @Description : 오토캠핑장 실시간 예약 크롤링
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
	
	function validator(f) {
		if ($('input[name="gubun"]:checked').length == 0) {
			HHCommon.Alert("구군을 선택 해주십시오.");
	    	return false;
		}
		else {
			if ($('input[name="gubun"]:checked').val() == 1 || $('input[name="gubun"]:checked').val() == 5) {
				if ($('input[name="campPlace"]:checked').length == 0) {
			    	HHCommon.Alert("캠핑장 장소를 하나 이상 선택하십시오.");
			    	return false;
				}
			}
		}
		if (!f.resvDate.value) {
	    	HHCommon.Alert("예약일자를 선택 해주십시오.", f.resvDate);
	    	return false;
		}
		if ($('input[name="printAllMode"]:checked').length == 0) {
			HHCommon.Alert("예약 확인 출력을 선택 해주십시오.");
	    	return false;
		}
		/* if (!f.nId.value) {
	    	HHCommon.Alert("로그인 아이디를 입력 해주십시오.", f.nId);
	    	return false;
		}
		if (!f.nPw.value) {
	    	HHCommon.Alert("로그인 비밀번호를 입력 해주십시오.", f.nPw);
	    	return false;
		} */
		crawlingProcModal(function(confirm) {
			if (confirm) {
				if ($('input[name="gubun"]:checked').val() == 'A') {
					resvAllCrawling();
				}
				else if ($('input[name="gubun"]:checked').val() == 1) {
					jungguCrawling();
				}
				else if ($('input[name="gubun"]:checked').val() == 2) {
					dongguCrawling();
				}
				else if ($('input[name="gubun"]:checked').val() == 3) {
					bukguCrawling();
				}
				else if ($('input[name="gubun"]:checked').val() == 4) {
					uljuCrawling();
				} 	
				else if ($('input[name="gubun"]:checked').val() == 5) {
					uljuReportCrawling();
				} 	 	
				else if ($('input[name="gubun"]:checked').val() == 6) {
					dangsaCrawling();
				} 	
			}
		});
		return false;
	}
	
	function resvAllCrawling() {
		var campPlaceList = [];
		/* $('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		}); */
		campPlaceList.push('1');
		campPlaceList.push('2');
		campPlaceList.push('3');
		campPlaceList.push('4');
		campPlaceList.push('5');
		campPlaceList.push('6');
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "day"      		: $('#resvDate').val(),
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
        $.ajax({
			type: 'POST',  
			url: './jungguCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />전체 예약확인은 데이터 수집 과정에 다소 시간이 소요됩니다.<br />중구 야영,휴양시설 예약상태를 확인하고 있습니다. (1/5)</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('중구 야영,휴양시설 예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>중구 야영,휴양시설 예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	
		    	
				$.ajax({
					type: 'POST',  
					url: './dongguCrawling.do',
					data: dataParams,
					dataType: 'text',
					beforeSend: function() {
				    	$('#holdonsMsg').html('잠시만 기다려주세요.<br />전체 예약확인은 데이터 수집 과정에 다소 시간이 소요됩니다.<br />동구 대왕암공원 캠핑장 예약상태를 확인하고 있습니다. (2/5)');
					},   
					complete: function() {},
					error: function() {
						HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
						$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
						HoldOn.close();
					}
				}).done(function(result) {
					$('#holdonsMsg').html('동구 대왕암공원 캠핑장 예약상태 결과를 출력중입니다.');
					$('#resultConsole').append('<div>동구 대왕암공원 캠핑장 예약상태 결과를 출력중입니다.</div>');
					var data = $.parseJSON(result);
					
					if (data.flag == true) {
						$('#resultResvDiv').show();
						$('#resultResv').append('<div>' + data.result + '</div>');
				    	
				    	
						$.ajax({
							type: 'POST',  
							url: './bukguCrawling.do',
							data: dataParams,
							dataType: 'text',
							beforeSend: function() {
						    	$('#holdonsMsg').html('잠시만 기다려주세요.<br />전체 예약확인은 데이터 수집 과정에 다소 시간이 소요됩니다.<br />북구 강동오토캠핑장 예약상태를 확인하고 있습니다. (3/5)');
							},   
							complete: function() {},
							error: function() {
								HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
								$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
								HoldOn.close();
							}
						}).done(function(result) {
							$('#holdonsMsg').html('북구 강동오토캠핑장 예약상태 결과를 출력중입니다.');
							$('#resultConsole').append('<div>북구 강동오토캠핑장 예약상태 결과를 출력중입니다.</div>');
							var data = $.parseJSON(result);
							if (data.flag == true) {
								$('#resultResvDiv').show();
								$('#resultResv').append('<div>' + data.result + '</div>');
								
								
								$.ajax({
									type: 'POST',  
									url: './uljuCrawling.do',
									data: dataParams,
									dataType: 'text',
									beforeSend: function() {
								    	$('#holdonsMsg').html('잠시만 기다려주세요.<br />전체 예약확인은 데이터 수집 과정에 다소 시간이 소요됩니다.<br />울주군 신불산 군립공원 야영장 예약상태를 확인하고 있습니다. (4/5)');
									},   
									complete: function() {},
									error: function() {
										HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
										$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
										HoldOn.close();
									}
								}).done(function(result) {
									$('#holdonsMsg').html('울주군 신불산 군립공원 야영장 예약상태 결과를 출력중입니다.');
									$('#resultConsole').append('<div>울주군 신불산 군립공원 야영장 예약상태 결과를 출력중입니다.</div>');
									
									var data = $.parseJSON(result);
									if (data.flag == true) {
										$('#resultResvDiv').show();
										$('#resultResv').append('<div>' + data.result + '</div>');
										
										
										campPlaceList = [];
										campPlaceList.push('a');
										campPlaceList.push('b');
										campPlaceList.push('c');
										campPlaceList.push('d');
										campPlaceList.push('e');
										campPlaceList.push('f');

								        dataParams = {
								            "gubun"      	: $('input[name="gubun"]:checked').val(), 
								            "campPlaceList" : campPlaceList,
								            "day"      		: $('#resvDate').val(),
								            "resvDate"      : $('#resvDate').val(),
								            "conNights"     : $('input[name="conNights"]:checked').val(),
								            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
								            "nId"      		: $('#nId').val(),
								            "nPw"      		: $('#nPw').val(),
								            "debug"      	: $('input[name="debug"]:checked').val()
								        };
										
										$.ajax({
											type: 'POST',  
											url: './uljuReportCrawling.do',
											data: dataParams,
											dataType: 'text',
											beforeSend: function() {
										    	$('#holdonsMsg').html('잠시만 기다려주세요.<br />전체 예약확인은 데이터 수집 과정에 다소 시간이 소요됩니다.<br />울주해양레포츠센터 예약상태를 확인하고 있습니다. (5/5)');
											},   
											complete: function() {},
											error: function() {
												HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
												$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
												HoldOn.close();
											}
										}).done(function(result) {
											$('#holdonsMsg').html('울주해양레포츠센터 예약상태 결과를 출력중입니다.');
											$('#resultConsole').append('<div>울주해양레포츠센터 예약상태 결과를 출력중입니다.</div>');
											
											var data = $.parseJSON(result);
											if (data.flag == true) {
												$('#resultResvDiv').show();
												$('#resultResv').append('<div>' + data.result + '</div>');
										    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
											}
											else {
										    	HHCommon.Alert(data.msg);
											}
									    	HoldOn.close();
										});
									}
									else {
								    	HHCommon.Alert(data.msg);
								    	HoldOn.close();
									}
								});
							}
							else {
						    	HHCommon.Alert(data.msg);
						    	HoldOn.close();
							}
						});
					}
					else {
				    	HHCommon.Alert(data.msg);
				    	HoldOn.close();
					}
				});
			}
			else {
		    	HHCommon.Alert(data.msg);
		    	HoldOn.close();
			}
		});
        
		/* $.ajax({
			type: 'POST',  
			url: './resvAllCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">전체 크롤링 작업은 다소 많은 시간이 소요될 수 있습니다.<br />잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		}); */
	}
	
	function jungguCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './jungguCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	var campLayout = '';
		    	if ($('input[name="campPlace"]').eq(0).is(':checked')) {
		    		campLayout += '<div style="background:gray;padding:5px;"><img src="/images/camp/junggu/junggu_plan1.png" alt="" style="width:100%;" /></div>'; 	
		    	}
		    	if ($('input[name="campPlace"]').eq(1).is(':checked')) {
		    		campLayout += '<div style="background:gray;padding:5px;"><img src="/images/camp/junggu/junggu_plan2.png" alt="" style="width:100%;" /></div>';	
		    	}
		    	if ($('input[name="campPlace"]').eq(2).is(':checked')) {
		    		campLayout += '<div style="background:gray;padding:5px;"><img src="/images/camp/junggu/junggu_plan3.png" alt="" style="width:100%;" /></div>';	
		    	}
		    	if ($('input[name="campPlace"]').eq(3).is(':checked')) {
		    		campLayout += '<div style="background:gray;padding:5px;"><img src="/images/camp/junggu/junggu_plan4.png" alt="" style="width:100%;" /></div>';	
		    	}
		    	if ($('input[name="campPlace"]').eq(4).is(':checked')) {
		    		campLayout += '<div style="background:gray;padding:5px;"><img src="/images/camp/junggu/junggu_plan5.png" alt="" style="width:100%;" /></div>';	
		    	}
		    	$('#campLayout').html(campLayout);
		    	$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function dongguCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './dongguCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	$('#campLayout').html('<div style="padding:5px;"><img src="/images/camp/donggu/donggu_plan1.png" alt="" style="width:100%;" /></div>');
		    	$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function uljuCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './uljuCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			
			var data = $.parseJSON(result);
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	$('#campLayout').html('<div style="padding:5px;"><img src="/images/camp/ulju/ulju_plan1.png" alt="" style="width:100%;" /></div>');
		    	$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function bukguCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './bukguCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	$('#campLayout').html('<div style="padding:5px;"><img src="/images/camp/bukgu/bukgu_plan1.png" alt="" style="width:100%;" /></div>');
		    	$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function dangsaCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "campPlaceList" : campPlaceList,
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './dangsaCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			var data = $.parseJSON(result);
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	$('#campLayout').html('<div style="padding:5px;"><img src="/images/camp/bukgu/camping_ocean_zone.jpg" alt="" style="width:100%;" /></div>');
		    	$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function subIdx(val) {
		if (!$('#nId').val()) {
	    	HHCommon.Alert("로그인 아이디를 입력 해주십시오.");
	    	return false;
		}
		if (!$('#nPw').val()) {
	    	HHCommon.Alert("로그인 비밀번호를 입력 해주십시오.");
	    	return false;
		} 
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(),
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: 'DEBUG',
            "resvIdx"      	: val
        };
        
        var url = '';
        if ($('input[name="gubun"]:checked').val() == '2') {
        	url = './dongguResv.do';
        }
        else if ($('input[name="gubun"]:checked').val() == '3') {
        	url = './bukguResv.do';
        }
        else if ($('input[name="gubun"]:checked').val() == '4') {
        	url = './uljuResv.do';
        }
        else if ($('input[name="gubun"]:checked').val() == '6') {
        	url = './dangsaResv.do';
        }
        
		$.ajax({
			type: 'POST',  
			url: url,
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">수동예약을 진행중입니다.<br />자동화된 프로그램이 최종 예약 페이지까지 보내줄 것입니다.<br />이후 각 입력란에 사용자의 정보를 입력하고 예약을 완료하십시오.</div>'
			    });
			},   
			complete: function() {},
			error: function(request, status, error) {
				//alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			var data = $.parseJSON(result);
			
			$('#holdonsMsg').html(data.msg);
			$('#resultConsole').append('<div>' + data.msg + '</div>');
			
			if (data.flag == true) {
				HHCommon.Alert(data.msg);
			}
			else {
				HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function uljuReportCrawling() {
		var campPlaceList = [];
		$('input[name="campPlace"]:checked').each(function(i) {
			campPlaceList.push($(this).val());
		});
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(), 
            "day"      		: $('#resvDate').val(),
            "campPlaceList" : campPlaceList,
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: $('input[name="debug"]:checked').val()
        };
        
		$.ajax({
			type: 'POST',  
			url: './uljuReportCrawling.do',
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				$('#resultConsole').html('');
				$('#resultResv').html('');
		    	$('#campLayoutDis').fadeOut('fast');
				
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">잠시만 기다려주세요.<br />데이터를 수집하여 예약일자를 확인하고 있습니다.</div>'
			    });
			},   
			complete: function() {},
			error: function() {
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			$('#holdonsMsg').html('예약상태 결과를 출력중입니다.');
			$('#resultConsole').append('<div>예약상태 결과를 출력중입니다.</div>');
			
			var data = $.parseJSON(result);
			if (data.flag == true) {
				$('#resultResvDiv').show();
				$('#resultResv').append('<div>' + data.result + '</div>');
		    	$('html, body').animate({scrollTop: $('#resultResv').offset().top}, 700);
		    	
		    	//$('#campLayout').html('<div style="padding:5px;"><img src="/images/camp/ulju/ulju_plan1.png" alt="" style="width:100%;" /></div>');
		    	//$('#campLayoutDis').fadeIn('fast');
			}
			else {
		    	HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
	function jungAutoResv(pageNm, val) {
		if (!$('#nId').val()) {
	    	HHCommon.Alert("로그인 아이디를 입력 해주십시오.");
	    	return false;
		}
		if (!$('#nPw').val()) {
	    	HHCommon.Alert("로그인 비밀번호를 입력 해주십시오.");
	    	return false;
		} 
		
        var dataParams = {
            "gubun"      	: $('input[name="gubun"]:checked').val(),
            "resvDate"      : $('#resvDate').val(),
            "conNights"     : $('input[name="conNights"]:checked').val(),
            "printAllMode"  : $('input[name="printAllMode"]:checked').val(),
            "nId"      		: $('#nId').val(),
            "nPw"      		: $('#nPw').val(),
            "debug"      	: 'DEBUG',
            "resvIdx"      	: val,
            "pageNm"      	: pageNm
        };
        
        var url = './jungguResv.do';
        
		$.ajax({
			type: 'POST',  
			url: url,
			data: dataParams,
			dataType: 'text',
			beforeSend: function() {
				HoldOn.open({ 
			    	theme : 'sk-folding-cube', 
			    	message : '<div id="holdonsMsg" style="padding-top:5px;">수동예약을 진행중입니다.<br />자동화된 프로그램이 최종 예약 페이지까지 보내줄 것입니다.<br />이후 각 입력란에 사용자의 정보를 입력하고 예약을 완료하십시오.</div>'
			    });
			},   
			complete: function() {},
			error: function(request, status, error) {
				//alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
				HHCommon.Alert('처리 중 오류가 발생되었습니다. 관리자에게 문의하십시오.');
				$('#resultConsole').append('<div>처리중 오류가 발생되었습니다. 관리자에게 문의하십시오.</div>');
				HoldOn.close();
			}
		}).done(function(result) {
			var data = $.parseJSON(result);
			
			$('#holdonsMsg').html(data.msg);
			$('#resultConsole').append('<div>' + data.msg + '</div>');
			
			if (data.flag == true) {
				HHCommon.Alert(data.msg);
			}
			else {
				HHCommon.Alert(data.msg);
			}
	    	HoldOn.close();
		});
	}
	
    $(function() {
    	if (${systemProp != 'true'}) $('#crawlingInfoModal').modal('show');
		$(".datepicker").datepicker({
            changeMonth: true, 
            changeYear: true,
			dateFormat: 'yy-mm-dd',
			prevText: '이전 달',
			nextText: '다음 달',
			monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			dayNames: ['일','월','화','수','목','금','토'],
			dayNamesShort: ['일','월','화','수','목','금','토'],
			dayNamesMin: ['일','월','화','수','목','금','토'],
			showMonthAfterYear: true,
			yearSuffix: '년',
			minDate: 0
		});
		$('input[name="gubun"]').click(function() {
			$('#placeDis > div').hide().eq(($(this).val()-1)).show();
			$('#placeDis input[name="campPlace"]').attr('checked', false);
		});
		
		setInterval(function() {
			var classStr = $('.page-header span.step_active').prop('class');
			$('.page-header span.step_active').attr('class', (classStr.indexOf('label-success') != -1) ? classStr.replace('success', 'default') : classStr.replace('default', 'success'));
		}, 1000);
    });     
	
</script>
<form id="formEl" name="formEl" action="./crawlingProc.do" method="post">
	<div class="contents">
		<div class="page-header">
			<h2><span class="glyphicon glyphicon-info-sign"></span> 지자체 오토캠핑장 실시간 예약 가능 여부 및 예약</h2>
			<%-- <p class="bg-warning" style="padding:15px;">
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 현재 구동 가능 드라이버 <strong class="text-primary"> (Ver. 113.0.5672.**)</strong>
				<a href="https://chromedriver.storage.googleapis.com/index.html" target="_blank" title="새창"><small><i class="glyphicon glyphicon-link"></i> 크롬드라이버 다운로드</small></a></span>
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 해당 크롤링 서비스를 이용하려면 사용자 PC에 설치된 크롬의 버전이 위 구동 가능 드라이버 버전과 동일한 버전이 설치되어야 합니다.</span>
			</p> --%>
			<p class="bg-warning" style="padding:15px;">
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 자동예약 : <strong class="text-primary">[정상동작중]</strong></span>
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 관리자가 설정한 자동예약 대상 : <strong class="text-primary">[북구 강동오토캠핑장, 울주군 신불산 군립공원 아영장]</strong></span>
				<span class="help-block"><i class="glyphicon glyphicon-exclamation-sign"></i> 현재 예약가능 상태 : 
					<span class="label label-default step">선착순 예약 오픈시 자동예약으로 선점 예약합니다.</span>
					<span class="label label-success step_active">즉시결제 기간으로 미결제분 및 취소분 수동예약만 가능합니다.</span>
				</span>
			</p>
		</div>
		<div class="panel panel-default">
  			<div class="panel-body">
				<div style="text-align:right;">
					<small><span class="glyphicon glyphicon-ok red-color"></span>는 필수 입력사항입니다.</small>
				</div>
				<table class="table table-striped table-bordered">
					<caption class="hidden">울산 지자체 오토캠핑장 실시간 예약(즉시 결제)</caption>
					<colgroup>
						<col style="width:18%;" />
						<col style="width:auto;" />
					</colgroup>
					<tbody>
				        <tr>
				            <th scope="row" class="primary-line-left">구군 선택 <span class="glyphicon glyphicon-ok red-color"></span></th>
				            <td class="form-inline">
				            	<label for="gubunAll" class="radio-inline"><input type="radio" id="gubunAll" name="gubun" value="A"><span class="label label-default">전체</span></label>
				            	<label for="gubun01" class="radio-inline"><input type="radio" id="gubun01" name="gubun" value="1"><span class="label label-default">중구 야영,휴양시설</span></label>
				            	<label for="gubun02" class="radio-inline"><input type="radio" id="gubun02" name="gubun" value="2"><span class="label label-default">동구 대왕암공원 캠핑장</span></label>
				            	<label for="gubun03" class="radio-inline"><input type="radio" id="gubun03" name="gubun" value="3"><span class="label label-default">북구 강동오토캠핑장</span></label>
				            	<label for="gubun04" class="radio-inline"><input type="radio" id="gubun04" name="gubun" value="4"><span class="label label-default">울주군 신불산 군립공원 아영장</span></label>
				            	<label for="gubun05" class="radio-inline"><input type="radio" id="gubun05" name="gubun" value="5" ><span class="label label-default">울주해양레포츠센터</span></label>
				            	<label for="gubun06" class="radio-inline"><input type="radio" id="gubun06" name="gubun" value="6"><span class="label label-default">북구 당사현대차오션캠프</span></label>
				            </td>
				        </tr>
				        <tr>
				            <th scope="row" class="primary-line-left">캠핑장 장소 <span class="glyphicon glyphicon-ok red-color"></span></th>
				            <td class="form-inline" id="placeDis">
				            	<div style="display:none;">
				            		<label for="campPlace01_1" class="checkbox-inline"><input type="checkbox" id="campPlace01_1" name="campPlace" value="1"><span class="label label-default">입화산 제2오토캠핑장</span></label>
					            	<label for="campPlace02_1" class="checkbox-inline"><input type="checkbox" id="campPlace02_1" name="campPlace" value="2"><span class="label label-default">황방산 생태야영장</span></label>
					            	<label for="campPlace03_1" class="checkbox-inline"><input type="checkbox" id="campPlace03_1" name="campPlace" value="3"><span class="label label-default">태화연 캠핑장</span></label>
					            	<label for="campPlace05_1" class="checkbox-inline"><input type="checkbox" id="campPlace05_1" name="campPlace" value="4"><span class="label label-default">입화산 자연휴양림(야영장)</span></label>
					            	<label for="campPlace04_1" class="checkbox-inline"><input type="checkbox" id="campPlace04_1" name="campPlace" value="5"><span class="label label-default">입화산 자연휴양림(별뜨락 카라반)</span></label>
					            	
					            	<h4><i class="glyphicon glyphicon-exclamation-sign"></i> 예약시 절대 주의사항</h4>
				            		<p class="bg-warning" style="padding:15px;">
					            		<span class="help-block">즉시결제시 1개의 계정으로 2박 연박 예약이 가능</span>
					            	</p>
				            	</div>
				            	<div style="display:none;">
				            		<h4><i class="glyphicon glyphicon-exclamation-sign"></i> 예약시 절대 주의사항</h4>
				            		<p class="bg-warning" style="padding:15px;">
					            		<span class="help-block">즉시결제시 1개의 계정당 임시 저장은 1박만 가능하므로 연박 예약시 동시에 1박씩 예약하기 위해선 2개의 계정이 필요</span>
					            		<span class="help-block">1개의 계정으로 예약하려면 우선 1박 예약의 결제까지 완료하고 다시 1박을 진행해야 되는 시스템이기 때문에 연박 예약이 거의 사실상 불가능</span>
					            	</p>
				            	</div>
				            	<div style="display:none;">
				            		<%-- <label for="campPlace01" class="checkbox-inline"><input type="checkbox" id="campPlace01" name="campPlace" value="1"><span class="label label-default">일반데크</span></label>
				            		<label for="campPlace01" class="checkbox-inline"><input type="checkbox" id="campPlace01" name="campPlace" value="2"><span class="label label-default">필로티</span></label>
				            		<label for="campPlace01" class="checkbox-inline"><input type="checkbox" id="campPlace01" name="campPlace" value="3"><span class="label label-default">카라반</span></label> --%>
				            		
				            		<h4><i class="glyphicon glyphicon-exclamation-sign"></i> 예약시 절대 주의사항</h4>
				            		<p class="bg-warning" style="padding:15px;">
					            		<span class="help-block">즉시결제시 1개의 계정당 임시 저장은 1박만 가능하므로 연박 예약시 동시에 1박씩 예약하기 위해선 2개의 계정이 필요</span>
					            		<span class="help-block">1개의 계정으로 예약하려면 우선 1박 예약의 결제까지 완료하고 다시 1박을 진행해야 되는 시스템이기 때문에 연박 예약이 거의 사실상 불가능</span>
					            	</p>
				            	</div>
				            	<div style="display:none;">
				            		<%-- <label for="campPlace01" class="checkbox-inline"><input type="checkbox" id="campPlace01" name="campPlace" value="0"><span class="label label-default">작천정</span></label>
				            		<label for="campPlace02" class="checkbox-inline"><input type="checkbox" id="campPlace02" name="campPlace" value="1"><span class="label label-default">등억</span></label>
				            		<label for="campPlace03" class="checkbox-inline"><input type="checkbox" id="campPlace03" name="campPlace" value="2"><span class="label label-default">달빛</span></label> --%>
				            		
				            		<h4><i class="glyphicon glyphicon-exclamation-sign"></i> 예약시 절대 주의사항</h4>
				            		<p class="bg-warning" style="padding:15px;">
					            		<span class="help-block">즉시결제시 1개의 계정당 임시 저장은 1박만 가능하므로 연박 예약시 동시에 1박씩 예약하기 위해선 2개의 계정이 필요</span>
					            		<span class="help-block">1개의 계정으로 예약하려면 우선 1박 예약의 결제까지 완료하고 다시 1박을 진행해야 되는 시스템이기 때문에 연박 예약이 거의 사실상 불가능</span>
					            		<span class="help-block">관리자가 선착순 오픈 10시를 무시하고 30초전에 오픈하기도 함</span>
					            	</p>
				            	</div>
				            	<div style="display:none;">
				            		<label for="campPlace01_5" class="checkbox-inline"><input type="checkbox" id="campPlace01_5" name="campPlace" value="a"><span class="label label-default">A구역</span></label>
				            		<label for="campPlace02_5" class="checkbox-inline"><input type="checkbox" id="campPlace02_5" name="campPlace" value="b"><span class="label label-default">B구역</span></label>
				            		<label for="campPlace03_5" class="checkbox-inline"><input type="checkbox" id="campPlace03_5" name="campPlace" value="c"><span class="label label-default">C구역</span></label>
				            		<label for="campPlace04_5" class="checkbox-inline"><input type="checkbox" id="campPlace04_5" name="campPlace" value="d"><span class="label label-default">D구역</span></label>
				            		<label for="campPlace05_5" class="checkbox-inline"><input type="checkbox" id="campPlace05_5" name="campPlace" value="e"><span class="label label-default">E구역</span></label>
				            		<label for="campPlace06_5" class="checkbox-inline"><input type="checkbox" id="campPlace06_5" name="campPlace" value="f"><span class="label label-default">F구역</span></label>
				            	</div>
				            	<div style="display:none;">
				            		<h4><i class="glyphicon glyphicon-exclamation-sign"></i> 예약시 절대 주의사항</h4>
				            		<p class="bg-warning" style="padding:15px;">
					            		<span class="help-block">즉시결제시 1개의 계정당 임시 저장은 1박만 가능하므로 연박 예약시 동시에 1박씩 예약하기 위해선 2개의 계정이 필요</span>
					            		<span class="help-block">1개의 계정으로 예약하려면 우선 1박 예약의 결제까지 완료하고 다시 1박을 진행해야 되는 시스템이기 때문에 연박 예약이 거의 사실상 불가능</span>
					            	</p>
				            	</div>
				            </td>
				        </tr>
				        <tr>
				            <th scope="row" class="primary-line-left">예약일자 <span class="glyphicon glyphicon-ok red-color"></span></th>
				            <td class="form-inline">
				            	<input type="text" id="resvDate" name="resvDate" class="form-control datepicker" placeholder="YYYY-MM-DD" readonly />
				            	<span class="help-block"><small>ex) 확인이 필요한 예약일자를 입력(YYYY-MM-DD) 하시기 바랍니다.</small></span>
				            </td>
				        </tr>
				        <tr>
				            <th scope="row">연박 가능여부 확인</th>
				            <td class="form-inline">
				            	<label for="conNights" class="checkbox-inline"><input type="checkbox" id="conNights" name="conNights" value="Y"><span class="label label-primary">연박 (2박3일)</span></label>
				            	<span class="help-block"><small>ex) 확인 예약일자에서 연박(2박3일)을 확인하고자 할때 체크 해주십시오.</small></span>
				            </td>
				        </tr>
				        <tr>
				            <th scope="row" class="primary-line-left">예약 확인 출력 <span class="glyphicon glyphicon-ok red-color"></span></th>
				            <td class="form-inline">
				            	<label for="printAllMode01" class="radio-inline"><input type="radio" id="printAllMode01" name="printAllMode" value="Y" checked><span class="label label-primary">전체</span></label>
				            	<label for="printAllMode02" class="radio-inline"><input type="radio" id="printAllMode02" name="printAllMode" value="N"><span class="label label-success">예약가능만</span></label>
				            </td>
				        </tr>
				        <tr>
				            <th scope="row">로그인 정보</th>
				            <td class="form-inline">
				            	<label for="nId">아이디 : </label><input type="text" id="nId" name="nId" value="" class="form-control" placeholder="아이디" />
				            	<label for="nPw">비밀번호 : </label><input type="password" id="nPw" name="nPw" value="" class="form-control" placeholder="비밀번호" />
				            	<span class="help-block"><small>ex) 예약하고자 하는 캠핑장의 사이트 로그인 정보를 입력하세요. (예약확인 및 예약실행)</small></span>
				            </td>
				        </tr>
			        	<tr>
				            <th scope="row" class="primary-line-left">DEBUG <span class="glyphicon glyphicon-ok red-color"></span></th>
				            <td>
				                <label for="debugY" class="radio-inline"><input type="radio" id="debugY" name="debug" value="DEBUG" /><span class="label label-success">예</span></label> 
				                <label for="debugN" class="radio-inline"><input type="radio" id="debugN" name="debug" value="RUN" checked="checked" /><span class="label label-danger">아니요 (백그라운드에서 진행)</span></label> 
				            </td>
				        </tr>
					</tbody>
				</table>
			</div>
		</div>
  		<div id="buttonFieldSet" style="text-align:center;">
  			<button type="button" onclick="validator(document.getElementById('formEl'));" class="btn btn-info">
                <span><i class="glyphicon glyphicon-ok-circle"></i> 예약확인</span>
			</button>
			<button type="button" onclick="window.location.href='/logout.do';" class="btn btn-default">
                <span><i class="glyphicon glyphicon-log-out"></i> 로그아웃</span>
			</button>
		</div>
		
		<div style="margin-top:20px;display:none;" id="resultResvDiv">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-list-alt"></span> 현재 예약상태 <small>캠핑장의 예약상태 결과를 표시합니다.</small></h3>
				</div>
				<div class="panel-body panel-scroll" id="resultResv" style="height:100%;font-size:12px;"></div>
			</div>
		</div>
		
		<!-- <div style="margin-top:20px;">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-list-alt"></span> Console <small>현재 처리중인 처리 결과를 표시합니다.</small></h3>
				</div>
				<div class="panel-body panel-scroll" id="resultConsole" style="height:150px;overflow-y:auto;font-size:12px;"></div>
			</div>
		</div> -->
		<div id="campLayoutDis" style="margin-top:20px;display:none;">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><span class="glyphicon glyphicon-list-alt"></span> 배치도 <small></small></h3>
				</div>
				<div class="panel-body panel-scroll" id="campLayout" style="height:100%;font-size:12px;"></div>
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
				<p><strong style="color:#FF3C44;">캠핑장 홈페이지의 접속 상태가 원활하지 않으면 다소 시간이 소요되거나 확인 중 오류가 발생할수도 있습니다.</strong></p>
				<p><span style="color:#2F70A9;">예약확인을 진행하시겠습니까?</span></p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal" id="crawlingProcModalClose">닫기</button>
				<button type="button" class="btn btn-primary" id="crawlingProcModalSubmit" data-loading-text="수집중..." autocomplete="off">확인</button>
			</div>
		</div>
	</div>
</div>