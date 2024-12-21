/**
 *
 * @file	: HHCommon
 * @author	: Shin-Hyunho
 * @brief	: 
 * @date    : 2015/06/03
 * @see     : 공통 
 **/

(function() {
	HHCommon = {
		/**
		 * form 요소 첫번째 엘리먼트 자동 포커스
		 * @param form el
		 * @return 
		 */
		FormTextEl_FirstAutoFocus: function(el) {
			$(el).find('input[type="text"]:first').focus();
		},
		/**
		 * Ajax 공통
		 * @param intype, inurl, indata, indataType
		 * @return ajax object
		 */
		Ajax: function(intype, inurl, indata, indataType) {
			var ajaxobj = $.ajax({  
				type: 'POST',  
				url: inurl,
				data: indata,
				dataType: indataType,
				beforeSend: function() {
					// ajax 시작을 알리는 로딩 이미지 추가
					
				},   
				complete: function() {
					
				},
				error: function() {
					HHCommon.Alert('서버와 통신중 오류가 발생 하였습니다.');
				}
			});
			return ajaxobj;
		},
		/**
		 * Alert 공통
		 * @param msg
		 * @return 
		 */
		Alert: function(msg) {
			$('#alertModal .modal-body').html(msg);
			$('#alertModal').modal('show');
		},
		Alert: function(msg, el) {
			$('#alertModal .modal-body').html(msg);
			$('#alertModal').modal('show');
			
			if (el != null) {
				$('#alertModal').on('hidden.bs.modal', function () {
					$(el).focus();
				});
			}
		},
		/**
		 * Confirm 공통
		 * @param msg, callback
		 * @return callback
		 */
		Confirm: function(msg, callback) {
			$('#confirmModal').modal({show:true,
                backdrop:true,
                keyboard:false
			});
			$('#confirmModal .modal-body').html(msg);
			$('#confirmModalClose').unbind().bind('click', function(){
				$('#confirmModal').modal('hide');
				if (callback) callback(false);
			});
			$('#confirmModalSubmit').unbind().bind('click', function(){
				$('#confirmModal').modal('hide');
				if (callback) callback(true);
			});
		},
		/**
		 * 비밀번호 유효성 체크 (9~20 영문 대소문자, 최소 1개의 숫자 혹은 특수 문자를 포함")
		 * @param str
		 * @return boolean
		 */
		PasswordPassCheck: function(str) {
			var reg = /^(?=.*[a-zA-Z])((?=.*\d)|(?=.*\W)).{9,20}$/;
			return !reg.test(str) ? false : true;
		},
		/**
		 * 이메일 유효성 체크
		 * @param str
		 * @return boolean
		 */
		EmailPassCheck: function(str) {
			var reg = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
			return !reg.test(str) ? false : true;
		},
		/**
		 * IP 유효성 체크
		 * @param str
		 * @return boolean
		 */
		IpAddressCheck: function(str) {
			var reg = /\b(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))\b$/;
			return !reg.test(str) ? false : true;
		},
		/**
		 * 전화번호 하이픈 자동생성
		 * @param element
		 * @return 
		 */
		AutoHypenPhone: function(el) {
			$(el).bind('keydown keyup', function() {
				var str = $(this).val().replace(/[^0-9]/g, '');
				var tmp = '';
				if( str.length < 4){
					tmp = str;
				}else if(str.length < 7){
					tmp += str.substr(0, 3);
					tmp += '-';
					tmp += str.substr(3);
				}else if(str.length < 11){
					tmp += str.substr(0, 3);
					tmp += '-';
					tmp += str.substr(3, 3);
					tmp += '-';
					tmp += str.substr(6);
				}else{				
					tmp += str.substr(0, 3);
					tmp += '-';
					tmp += str.substr(3, 4);
					tmp += '-';
					tmp += str.substr(7);
				}
				$(this).val(tmp);
			});
		},
		/**
		 * 숫자만 입력가능하도록 수정처리하여 반환
		 */
		NumberNotClear: function(el) {
			$(el).bind('keydown keyup', function() {
				var str = $(this).val().replace(/[^0-9]/g, '');
				$(this).val(str);
			});
		},
		/**
		 * 숫자인지 확인
		 */
		NumberCheck: function(str) {
			var reg = /[^0-9]/;
			return reg.test(str) ? false : true;
		},
		/**
		 * 숫자의 길이만큼 0을 채운다.
		 */
		ZeroFill: function(n, digits) {
			var zero = '';
		    n = n.toString();
		 
		    if (n.length < digits) {
		        for (i = 0; i < digits - n.length; i++)
		            zero += '0';
		    }
		    return zero + n;
		},
		/**
		 * 길이만큼 0을 끝에 채워서 반환
		 */
		ZeroFillEnd: function(inputvalue, demandLength) {
			var spaceValue = "";
			for (var i = 0; i < demandLength-inputvalue.length;i++){
				spaceValue += "0";
			}
			return inputvalue + spaceValue;
		},
		StrReplace: function(org, dest, str) {
			var reg = new RegExp(org, "g");
			return str.replace(reg, dest);
		},
		/**
		 * 콤마 제거 
		 */
		UnNumberFormat: function(num) {
			return (num.replace(/\,/g,""));
		},
		/**
		 * 콤마 처리후 리턴
		 * @param: num
		 * @return
		 */
		NumberFormat: function(num) {
			var pattern = /(-?[0-9]+)([0-9]{3})/;
			while(pattern.test(num)) {
				num = num.replace(pattern,"$1,$2");
			}
			return num;
		},
		/**
		 * 콤마 AddEvent
		 * @param: el
		 * @return
		 */
		NumberFormatAddEvent: function(el) {
			$(el).bind('keydown keyup', function() {
				console.log(HHCommon.UnNumberFormat($(this).val()));
				$(this).val(HHCommon.NumberFormat(HHCommon.UnNumberFormat($(this).val())));
			});
		},
		/**
		 * 리얼타임
		 */
		ThisStartClock: function() {
			var week = new Array('일','월','화','수','목','금','토');
			var today = new Date();
			var y = today.getFullYear();
			var M = HHCommon.ZeroFill(today.getMonth() + 1, 2);
			var d = HHCommon.ZeroFill(today.getDate(), 2);
			//var h = HHCommon.ZeroFill(today.getHours(), 2);
			var h = today.getHours();
			var m = HHCommon.ZeroFill(today.getMinutes(), 2);
			var s = HHCommon.ZeroFill(today.getSeconds(), 2);
			var hstr = "";
			if (h < 12)  {
				hstr = "오전 ";
			} else {
				hstr = "오후 ";
				h = h % 12;
			}
			
			var date = y + "년 " + M + "월 " + d + "일 " + week[today.getDay()] + "요일 ";
			$('#dis_date').html(date);
			
			var time = hstr + h + ":" + m + ":" + s;
			$('#dis_clock').html(time);
			
			setTimeout(function(){
				HHCommon.ThisStartClock();
			}, 500);
		},
		detectDevTool: function(contextPath) {
			$(document).bind('contextmenu', function(e) { return false; });
			$(document).bind('selectstart', function() { return false; });
			$(document).bind('dragstart', function() { return false; });
			$(document).keydown(function(e) {
				//alert(e.keyCode);
				//if(e.keyCode === 123 || (e.ctrlKey && e.shiftKey && e.keyCode == 73) || (e.ctrlKey && e.shiftKey && e.keyCode == 74) || (e.ctrlKey && e.shiftKey && e.keyCode == 67)){
				if(e.keyCode === 123) {
					e.preventDefault();
					e.returnValue = false;
				}
			});
			
			function detectDevTool(allow) {
		  		if(isNaN(+allow)) allow = 100; // 시간차 간격
				var start = +new Date();
			    debugger;
				var end = +new Date();
			    if(isNaN(start) || isNaN(end) || end - start > allow) {
					//alert('DEVTOOLS detected. all operations will be terminated.');
					//document.write('DEVTOOLS detected.');
					//alert('개발자도구가 감지되었습니다.');
					//history.back(-1);
					window.location.replace(contextPath + '/storyCmsDevTool.jsp');
				}
			}

			if(window.attachEvent) {
				if (document.readyState === "complete" || document.readyState === "interactive") {
					detectDevTool();
					window.attachEvent('onresize', detectDevTool);
					window.attachEvent('onmousemove', detectDevTool);
					window.attachEvent('onfocus', detectDevTool);
					window.attachEvent('onblur', detectDevTool);
				} 
				else {
					setTimeout(argument.callee, 0);
				}
			} else {
				window.addEventListener('load', detectDevTool);
				window.addEventListener('resize', detectDevTool);
				window.addEventListener('mousemove', detectDevTool);
				window.addEventListener('focus', detectDevTool);
				window.addEventListener('blur', detectDevTool);
			}
		}
	},
	/**
	 * 타임아웃 자동 로그아웃
	 * ID를 가진 객체에 data-contextPath, data-maxInactiveInterval 속성을 정의
	 * @attr : data-contextPath는 서버의 contextPath의 값
	 * @attr : data-maxInactiveInterval는 WAS에 설정된 세션타임값 (초단위 600 / 60 = 10분)
	 */
	HHLoginExpireSesstion = {
		_ContextPath: null,
		_MaxInactiveTime: 0,
		_MainThread: null,
		_ToggleThread: null,
		_Element: null,
		_WarningTime: 0,
		Init: function() {
			HHLoginExpireSesstion._Element = $('#loginExpireSesstion'); 
			HHLoginExpireSesstion._ContextPath = HHLoginExpireSesstion._Element.attr('data-contextPath');
			HHLoginExpireSesstion._MaxInactiveTime = HHLoginExpireSesstion._Element.attr('data-maxInactiveInterval');
			HHLoginExpireSesstion._WarningTime = parseInt(((HHLoginExpireSesstion._MaxInactiveTime / 60) * 10) / 100);
			HHLoginExpireSesstion._MainThread = setInterval("HHLoginExpireSesstion.CountRun()", 1000);
			
			$('a[href="#loginExpireSesstionRefresh"]').bind('click', function() {
				HHLoginExpireSesstion.loginExpireSesstion();
				return false;
			});
			$('a[href="#loginExpireSesstionRefresh"]').bind('mouseenter', function() {
				$(this).find('i').attr('class', 'glyphicon glyphicon-refresh');
				return false;
			});
			$('a[href="#loginExpireSesstionRefresh"]').bind('mouseleave', function() {
				$(this).find('i').attr('class', 'fa fa-clock-o');
				return false;
			});
		},
		Logout: function(contextPath) {
			alert("설정된 자동로그아웃시간(" + parseInt(HHLoginExpireSesstion._Element.attr('data-maxInactiveInterval') / 60) + "분)을 초과하는 동안 사용자의 동작이 없어 자동로그아웃 처리되었습니다.");
			window.location.replace(HHLoginExpireSesstion._ContextPath + "/logout.do");
		},
		CountDisplay: function(val) {
			if (parseInt(HHLoginExpireSesstion._MaxInactiveTime / 60) <= HHLoginExpireSesstion._WarningTime) {
				HHLoginExpireSesstion._Element.parent('li').addClass('warning');
				
				if (HHLoginExpireSesstion._ToggleThread == null) {
					HHLoginExpireSesstion._ToggleThread = setInterval(function(){
						//HHLoginExpireSesstion._Element.parent('li').toggle();
						HHLoginExpireSesstion._Element.parent('a').toggleClass('warning');
					}, 500);
				}
			}
			else {
				HHLoginExpireSesstion._Element.parent('a').removeClass('init').addClass('run');
			}
			HHLoginExpireSesstion._Element.text(val);
		},
		CountRun: function() {
			HHLoginExpireSesstion.CountDisplay(HHLoginExpireSesstion.TimeFormat(HHLoginExpireSesstion._MaxInactiveTime));
			HHLoginExpireSesstion._MaxInactiveTime--;
			
			if (HHLoginExpireSesstion._MaxInactiveTime < 0) {
				clearInterval(HHLoginExpireSesstion._MainThread);
				clearInterval(HHLoginExpireSesstion._ToggleThread);
				HHLoginExpireSesstion.Logout();
			}
		},
		TimeFormat: function(time) {
			var hour = 0;
			var min = 0;
			var sec = 0;
			if (time > 0) {
				min = parseInt(time / 60);
				sec = time % 60;
				if (min > 60) {
					hour = parseInt(min / 60);
					min = min % 60;
				}
			}
			sec = (sec < 10) ? "0" + sec : sec;
			min = (min < 10) ? "0" + min : min;
			
			return hour + ":" + min + ":" + sec;
		},
		loginExpireSesstion: function() {
			var sendUrl = HHLoginExpireSesstion._ContextPath + '/dashboard.do';
			var data = {};
	    	var jqxhr = HHCommon.Ajax('POST', sendUrl, data, 'text');
			jqxhr.done(function(data) {
				HHLoginExpireSesstion._Element.text(HHLoginExpireSesstion.TimeFormat(HHLoginExpireSesstion._Element.attr('data-maxInactiveInterval')));
				HHLoginExpireSesstion._MaxInactiveTime = HHLoginExpireSesstion._Element.attr('data-maxInactiveInterval');
				HHLoginExpireSesstion._WarningTime = parseInt(((HHLoginExpireSesstion._MaxInactiveTime / 60) * 10) / 100);
				clearInterval(HHLoginExpireSesstion._ToggleThread);
				HHLoginExpireSesstion._ToggleThread = null;
				HHLoginExpireSesstion._Element.parent('a').removeClass().addClass('run');
			});
		}
	}
})(); 

$(function() {
	
	// 자동로그아웃
	//HHLoginExpireSesstion.Init();
	//HHCommon.detectDevTool('');
});
