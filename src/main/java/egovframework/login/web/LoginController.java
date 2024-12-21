package egovframework.login.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.common.util.WriterUtil;

/**
 * 관리자 로그인
 * 
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 * 
 * 2018.12.05 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2018.12.05
 * @version 1.0
 *
 */
@Controller
public class LoginController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	/**
	 * 로그인 페이지
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/login.do")
	public String login(
			HttpServletRequest req, 
			HttpServletResponse res, 
			ModelMap model) throws Exception{
		
		String path = "egovframework/story/login";
		LOGGER.info("path : {}", path);
		return path;
	}
	
	/**
	 * 로그인 처리
	 * @param req
	 * @param res
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loginProc.do")
	public String loginProc(
			@RequestParam("userId") String userId,
			@RequestParam("userPw") String userPw,
			HttpServletRequest req, 
			HttpServletResponse res,
			HttpSession session,
			ModelMap model) throws Exception{
		
		if (!"admin".equals(userId) && !"hh1225".equals(userId)) {
			WriterUtil.flushJsAlertAndHistoryBack(res, "로그인 정보를 확인하여 주십시오!.");
			return null;
		}
		
		// 비밀번호 확인
		if (!"9306".equals(userPw)) {			
			WriterUtil.flushJsAlertAndHistoryBack(res, "로그인 정보를 확인하여 주십시오!.");
			return null;
		}
		
		// 세션 초기화
		HttpSession sessionInfo = req.getSession(false);
		if (sessionInfo != null) sessionInfo.invalidate();
		sessionInfo = req.getSession(true);
		
		// 최고관리자
		if ("hh1225".equals(userId)) {
			sessionInfo.setAttribute("IS_ADMINISTRATOR", true);
		}
		
		// 로그인 여부
		sessionInfo.setAttribute("IS_LOGIN", true);
		
		res.sendRedirect(req.getContextPath() + "/autoCamp/resv/crawling.do");
		//res.sendRedirect(req.getContextPath() + "crawlingList.jsp");
		return null;
	}

	/**
	 * 로그아웃
	 * @param req
	 * @param res
	 * @param session
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
   	public String logout(HttpServletRequest req, 
			HttpServletResponse res,
			HttpSession session) throws Exception {
		
		req.getSession().removeAttribute("IS_ADMINISTRATOR");
		req.getSession().removeAttribute("IS_LOGIN");
       	session.invalidate();
       	
       	res.sendRedirect(req.getContextPath() + "/login.do");
        return null;
   	}
	
}
