package egovframework.crawling.camp.common;

/**
 * 상수 관련 정의 
 *
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2016.04.11 신현호
 * 최초 생성
 * 
 * 2019.01.10 신현호
 * 계약심사 통보처리일 7일 -> 10일로 변경
 * 
 * 2019.12.24 정지인
 * 일상감사(사전컨설팅) 통보처리일 7일 -> 30일로 변경
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2016.04.11
 * @version 1.0
 */
public class Constant {

	public final static boolean DEBUG = true;
	
	public final static int INTERRUPTED_TIME = 500;
	
	public final static String WEB_DRIVER_ID = "webdriver.chrome.driver"; 
	
	public final static String WEB_DRIVER_PATH = "D:\\Dev\\workspace-egov3.7\\crawlingService\\src\\main\\webapp\\WEB-INF\\lib\\chromedriver.exe";
}
