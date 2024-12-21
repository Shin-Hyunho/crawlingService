package egovframework.common.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.common.util.StringUtil;

@Controller("LoginInterceptor")
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static final String TAG = "AdminLoginInterceptor";
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);
	
	private static final String[] remoteWhiteList = {"127.0.0.1", "211.205.153.165", "106.247.251.2", "192.168.55.31", "211.215.85.229"};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// 접속지 IP
		String remoteIp = StringUtil.getRemoteAddr(request);
		
		try {
			// 최고관리자는 모든 조건을 무시
			boolean administrator = request.getSession().getAttribute("IS_ADMINISTRATOR") == null ? false : (Boolean) request.getSession().getAttribute("IS_ADMINISTRATOR");
			if (administrator) {
				return true;
			}
			else {
				// IP 확인
				List<String> remoteList = new ArrayList<>(Arrays.asList(remoteWhiteList));
				if( remoteList.contains(remoteIp) ) {
					
					// 로그인 여부
					boolean isLogin = (Boolean) request.getSession().getAttribute("IS_LOGIN");
					if (isLogin) {				
						return true;
					}
					else {
						request.getSession().invalidate();
						response.sendRedirect(request.getContextPath() + "/login.do");
						System.out.println("관리자 로그인 세션정보가 없습니다!.");
						
						return false;
					} 
				}
				else {
					request.getSession().invalidate();
					response.sendRedirect(request.getContextPath() + "/accessFail.jsp");
					System.out.println(remoteIp + " 허가받지 않는 접속지 IP입니다!. [" + remoteIp + "]");

					//throw new RuntimeException("허가받지 않는 접속지 IP입니다!. [" + remoteIp + "]");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/login.do");
			return false;
		}
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception { }

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception { }
}
