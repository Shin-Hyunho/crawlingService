package egovframework.common.util;

import javax.servlet.http.HttpServletRequest;

import egovframework.common.SessionKey;
import egovframework.common.manager.LoginManager;

/**
 * LoginManager에서 생성된 세션 util
 * 
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2016.04.11 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2016.04.11
 * @version 1.0
 */
public class SessionUtil {
	
	public static String getSessionAttrAsString(HttpServletRequest request, String key) {
		return (String) request.getSession(Boolean.TRUE).getAttribute(key);
	}

	/**
	 * LoginManager에서 로그인 정보를 가져와서 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static Object getSessionObj(HttpServletRequest request) {
		Object retObj = null;
		if (request != null && request.getSession().getAttribute(SessionKey.LOGIN_INFO.getKey()) != null) {
			retObj = LoginManager.getInstance().getLoginInfo(request.getSession().getId());
		}
		return retObj;
	}

	/**
	 * LoginManager에서 사용자 로그인 정보를 가져와서 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static Object getUserSessionObj(HttpServletRequest request) {
		Object retObj = null;
		if (request != null && request.getSession().getAttribute(SessionKey.USER_LOGIN_INFO.getKey()) != null) {
			retObj = LoginManager.getInstance().getLoginInfo(SessionKey.USER_SESSIONID_FLAG.getKey() + request.getSession().getId());
		}
		return retObj;
	}

	/**
	 * LoginManager에서 관리자 로그인 정보를 가져와서 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static Object getAdminSessionObj(HttpServletRequest request) {
		Object retObj = null;
		if (request != null && request.getSession().getAttribute(SessionKey.ADMIN_LOGIN_INFO.getKey()) != null) {
			retObj = LoginManager.getInstance().getLoginInfo(SessionKey.ADMIN_SESSIONID_FLAG.getKey() + request.getSession().getId());
		}
		return retObj;
	}

	/**
	 * LoginManager에서 현재 사용자가 로그인이 되어있는지에 대한 정보를 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static boolean isLogin(HttpServletRequest request) {
		boolean bReturn = false;
		if (request != null && request.getSession().getAttribute(SessionKey.LOGIN_INFO.getKey()) != null) {
			bReturn = LoginManager.getInstance().isLogin(request.getSession().getId());
		}
		return bReturn;
	}

	/**
	 * LoginManager에서 현재 사용자가 로그인이 되어있는지에 대한 정보를 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static boolean isUserLogin(HttpServletRequest request) {
		boolean bReturn = false;
		if (request != null && request.getSession().getAttribute(SessionKey.USER_LOGIN_INFO.getKey()) != null) {
			bReturn = LoginManager.getInstance().isLogin(SessionKey.USER_SESSIONID_FLAG.getKey() + request.getSession().getId());
		}
		return bReturn;
	}

	/**
	 * LoginManager에서 현재 관리자가 로그인이 되어있는지에 대한 정보를 Return한다.
	 *
	 * @param request
	 * @return
	 */
	public static boolean isAdminLogin(HttpServletRequest request) {
		boolean bReturn = false;
		if (request != null && request.getSession().getAttribute(SessionKey.ADMIN_LOGIN_INFO.getKey()) != null) {
			bReturn = LoginManager.getInstance().isLogin(SessionKey.ADMIN_SESSIONID_FLAG.getKey() + request.getSession().getId());
		}
		return bReturn;
	}
	
	/**
	 * 본인인증 세션관련값을 일괄 삭제
	 * 
	 * @return
	 */
	public static void removeSessionCheckRealAuth(HttpServletRequest request) {
		request.getSession().removeAttribute(SessionKey.CHECKREAL_REQUESTSEQ.getKey()); 		// 본인인증 요청시 변조방지를 위한 생성값
		request.getSession().removeAttribute(SessionKey.CHECKREAL_RESPONSENUMBER.getKey()); 	// 본인확인 요청번호
		request.getSession().removeAttribute(SessionKey.CHECKREAL_AUTHTYPE.getKey());			// 인증수단                        
		request.getSession().removeAttribute(SessionKey.CHECKREAL_NAME.getKey());               // 성명                          
		request.getSession().removeAttribute(SessionKey.CHECKREAL_BIRTH.getKey());              // 생년월일                        
		request.getSession().removeAttribute(SessionKey.CHECKREAL_GENDER.getKey());             // 성별                          
		request.getSession().removeAttribute(SessionKey.CHECKREAL_NATIONALINFO.getKey());       // 국적 정보 - 0:내국인 / 1:외국인       
		request.getSession().removeAttribute(SessionKey.CHECKREAL_DUPINFO.getKey());            // 개인 회원의 중복가입여부 확인을 위해 사용되는 값 
		request.getSession().removeAttribute(SessionKey.CHECKREAL_CONNINFO.getKey());           // 주민등록번호와 1:1로 매칭되는 고유키       
	}

	/**
	 * 사용자의 IP를 return 해줌
	 *
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("x-forwarded-for");
		if (remoteAddr == null || remoteAddr.length() == 0 || remoteAddr.toLowerCase().equals("unknown")) {
			remoteAddr = request.getHeader("REMOTE_ADDR");
		}

		if (remoteAddr == null || remoteAddr.length() == 0 || remoteAddr.toLowerCase().equals("unknown")) {
			remoteAddr = request.getRemoteAddr();
		}

		return remoteAddr;
	}

	public static Boolean isSubAdminLogin(HttpServletRequest request) {
		boolean bReturn = false;
		if (request != null && request.getSession().getAttribute(SessionKey.ADMIN_SUB_LOGIN_INFO.getKey()) != null) {
			bReturn = LoginManager.getInstance().isLogin(request.getSession().getId()); // TODO
		}
		return bReturn;
	}

	public static <T> T getSessionAttr(HttpServletRequest request, String key, Class<T> clazz) {
		return clazz.cast(request.getSession(Boolean.TRUE).getAttribute(key));
	}
}
