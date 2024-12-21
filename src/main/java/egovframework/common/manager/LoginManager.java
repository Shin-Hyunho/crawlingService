package egovframework.common.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import egovframework.common.SessionKey;

/**
 * 세션(로그인) Manager
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
public class LoginManager implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = 1L;

	private static LoginManager loginManager = null;
	private static Hashtable<String, Object> loginUsers = new Hashtable<String, Object>();

	private LoginManager() {
		super();
	}

	public static synchronized LoginManager getInstance() {
		if (loginManager == null) {
			loginManager = new LoginManager();
		}
		return loginManager;
	}

	/**
	 * 해당 세션에 이미 로그인 되어있는지 검사
	 *
	 * @param sessionId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean isLogin(String sessionId) {
		boolean isLogin = false;
		Enumeration e = loginUsers.keys();
		String key = null;
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			if (sessionId.equals(key)) {
				isLogin = true;
			}
		}
		return isLogin;
	}

	/**
	 * 입력받은 아이디를 해시테이블에서 삭제.
	 *
	 * @param userId 사용자 아이디
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void removeSession(String userId) {
		Enumeration e = loginUsers.keys();
		HttpSession session = null;
		while (e.hasMoreElements()) {
			session = (HttpSession) e.nextElement();
			if (loginUsers.get(session).equals(userId)) {
				// 세션이 invalidate될때 HttpSessionBindingListener를
				// 구현하는 클레스의 valueUnbound()함수가 호출된다.
				session.invalidate();
			}
		}
	}

	/**
	 * 세션 생성
	 *
	 * @param session
	 * @param userInfo
	 */
	public void setSession(HttpSession session, Object loginInfo) {
		loginUsers.put(session.getId(), loginInfo);
		session.setAttribute(SessionKey.LOGIN_INFO.getKey(), loginInfo);
		session.setAttribute("login", getInstance());
	}

	/**
	 * 관리자 세션 생성
	 *
	 * @param session
	 * @param loginInfo
	 */
	public void setAdminSession(HttpSession session, Object loginInfo) {
		loginUsers.put(SessionKey.ADMIN_SESSIONID_FLAG.getKey() + session.getId(), loginInfo);
		session.setAttribute(SessionKey.ADMIN_LOGIN_INFO.getKey(), loginInfo);
		session.setAttribute("login", getInstance());
	}

	/**
	 * 사용자 세션 생성
	 *
	 * @param session
	 * @param loginInfo
	 */
	public void setUserSession(HttpSession session, Object loginInfo) {
		loginUsers.put(SessionKey.USER_SESSIONID_FLAG.getKey() + session.getId(), loginInfo);
		session.setAttribute(SessionKey.USER_LOGIN_INFO.getKey(), loginInfo);
		session.setAttribute("login", getInstance());
	}

	/**
	 * 서브 관리자 세션 생성 (차후 서브관리자를 이용할때 별도로 구현하기 바랍니다.)
	 *
	 * @param session
	 * @param loginInfo
	 */
	/*public void setSubAdminSession(HttpSession session, SubAdminVO loginInfo) {
		loginUsers.put(session.getId(), loginInfo);
		session.setAttribute(SessionKey.ADMIN_SUB_LOGIN_INFO.getKey(), loginInfo);
		session.setAttribute("login", getInstance());
	}*/
	
	/**
	 * 세션이 성립될때
	 *
	 * @param event
	 */
	public void valueBound(HttpSessionBindingEvent event) {
	}

	/**
	 * 세션 ID로 로그인 정보 구분
	 *
	 * @param sessionId
	 * @return
	 */
	public Object getLoginInfo(String sessionId) {
		return loginUsers.get(sessionId);
	}

	/**
	 * 현재 접속자수
	 *
	 * @return
	 */
	public int getUserCount() {
		return loginUsers.size();
	}

	/**
	 * 현재 접속되어 있는 사용자 정보 얻어오기
	 *
	 * @return retList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getLoginUserList() {
		Enumeration e = loginUsers.keys();
		String key = "";
		List retList = new ArrayList();
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			retList.add(loginUsers.get(key));
		}

		return retList;
	}

	/**
	 * 입력받은 세션Object로 아이디를 리턴한다.
	 *
	 * @param session : 접속한 사용자의 session Object
	 * @return String : 접속자 아이디
	 */
	public String getUserId(HttpSession session) {
		return (String) loginUsers.get(session);
	}

	/**
	 * 해당 아이디의 동시 사용을 막기위해서
	 * 이미 사용중인 아이디인지를 확인한다.
	 *
	 * @param userId 사용자 아이디
	 * @return boolean 이미 사용 중인 경우 true, 사용중이 아니면 false
	 */
	public boolean isUsing(String userId) {
		return loginUsers.containsValue(userId);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub

	}
}
