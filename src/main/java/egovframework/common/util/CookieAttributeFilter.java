package egovframework.common.util;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CookieAttributeFilter implements Filter{ 

	@Override 
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException { 
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		chain.doFilter(request, response);
		
		HttpSession session = ((HttpServletRequest) request).getSession(false);
		if (session != null) {
			addSameSite(httpServletResponse , "None");
			//System.out.println("=============================================================== CookieAttributeFilter");
		}
	} 
	
	@Override 
	public void init(FilterConfig filterConfig) throws ServletException { 
		// TODO Auto-generated method stub 
	} 
	
	@Override 
	public void destroy() { 
		// TODO Auto-generated method stub 
	} 
	
	private void addSameSite(HttpServletResponse response, String sameSite) {
		Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE); 
		boolean firstHeader = true; 
		for (String header : headers) {
			if (firstHeader) { 
				response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite)); 
				firstHeader = false; 
				continue; 
			} 
			response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite)); 
			//System.out.println("CookieAttributeFilter : ========================== " + String.format("%s; Secure; %s", header, "SameSite=" + sameSite));
		}
		//System.out.println("=============================================================== sameSite" + sameSite);
		
		// 크롬 Samsite LAX 설정을 NONE으로 변경 처리
		if (headers.isEmpty()) {
			HttpServletRequest request =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			String sessionid = request.getSession().getId();
			String contextPath = request.getContextPath();
			
			if (firstHeader) {
				response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; path=/; Secure; SameSite=None; HttpOnly=HttpOnly");
				response.addHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; path=" + contextPath + "; Secure; SameSite=None; HttpOnly=HttpOnly");
				//response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; Path=/u; Secure; SameSite=None");
				System.out.println("1. CookieAttributeFilter : ========================== addHeader JSESSIONID");
			}
			else {
				response.addHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; path=/; Secure; SameSite=None; HttpOnly=HttpOnly");
				response.addHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; path=" + contextPath + "; Secure; SameSite=None; HttpOnly=HttpOnly");
				System.out.println("2. CookieAttributeFilter : ========================== addHeader JSESSIONID");
			}
		}
	} 
}