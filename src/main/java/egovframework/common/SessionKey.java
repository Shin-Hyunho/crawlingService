package egovframework.common;

/**
 * 세션 속성(Prop)키 정의
 * 
 * 세션키를 하나로 관리하기 위함이 목적
 * 
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2017.04.10 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2017.04.10
 * @version 1.0
 */
public enum SessionKey {

	/* --------------------------------- 공통 정의 --------------------------------- */
	
	/** 공통 로그인 객세 속성 */
	LOGIN_INFO("LOGIN_INFO"),
	
	/** 본인인증 세션 속성키 */
	CHECKREAL_REQUESTSEQ("REQ_SEQ"),
	CHECKREAL_RESPONSENUMBER("CheckReal_ResponseNumber"),
	CHECKREAL_AUTHTYPE("CheckReal_AuthType"),
	CHECKREAL_NAME("CheckReal_Name"),
	CHECKREAL_BIRTH("CheckReal_Birth"),
	CHECKREAL_GENDER("CheckReal_Gender"),
	CHECKREAL_NATIONALINFO("CheckReal_NationalInfo"),
	CHECKREAL_DUPINFO("CheckReal_DupInfo"),
	CHECKREAL_CONNINFO("CheckReal_ConnInfo"),
	/*/ --------------------------------- 공통 정의 --------------------------------- */
	
	
	/* --------------------------------- 게시판 정의 --------------------------------- */
	/** 비밀번호 인증체크값 */
	BBS_VALID_PASSWORD_KEY("BBS_VALID_PASSWORD"),
	
	/** 비밀번호 인증글고유ID */
	BBS_VALID_PASSWORD_DATAID_KEY("BBS_VALID_PASSWORD_DATAID"),
	/*/ --------------------------------- 게시판 정의 --------------------------------- */
	
	
	/* --------------------------------- 사용자 정의 --------------------------------- */

	/** 사용자 세션 ID flag 속성키 */
	USER_SESSIONID_FLAG("USER_"),

	/** 사용자 로그인 객체 속성키 */
	USER_LOGIN_INFO("USER_LOGIN_INFO"),
	
	/*/ --------------------------------- 사용자 정의 --------------------------------- */
	
	
	/* --------------------------------- 관리자 정의 --------------------------------- */
	
	/** 관리자 세션 ID flag 속성키 */
	ADMIN_SESSIONID_FLAG("ADMIN_"),
	
	/** 관리자 로그인 객체 속성키 */
	ADMIN_LOGIN_INFO("ADMIN_LOGIN_INFO"),
	
	/** 행정인턴 선발 권한 유무 */
	INTERN_AUTH("INTERN_AUTH"),

	/** 관리자 서브 로그인 객체 정보 */
	ADMIN_SUB_LOGIN_INFO("SUB_ADMIN_LOGIN_INFO"),
	;
	
	/*/ --------------------------------- 관리자 정의 --------------------------------- */

	private String key;

	private SessionKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}