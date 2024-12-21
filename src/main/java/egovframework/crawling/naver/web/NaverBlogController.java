package egovframework.crawling.naver.web;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import egovframework.crawling.naver.service.NaverBlogCommentVO;

/**
 * 네이버 블로그 크롤링
 *
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2022.03.08 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2022.03.10
 * @version 1.0
 */
@Controller
@RequestMapping("/naver/blog")
public class NaverBlogController {
	private static final String TAG 	= "NaverBlogController";
	private static final Logger LOGGER 	= LoggerFactory.getLogger(NaverBlogController.class);
	
	// interrupted time
	private static final int INTERRUPTED_TIME 	= 1500;
	
	// 드라이버 ID
	private static final String WEB_DRIVER_ID 	= "webdriver.chrome.driver"; 
	
	// 드라이버 경로
	private static String WEB_DRIVER_PATH = "D:\\Dev\\workspace-egov3.7\\crawlingService\\src\\main\\webapp\\WEB-INF\\lib\\chromedriver.exe";

	/**
	 * 댓글 크롤링 정보입력
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/comment/crawling.*")
	public String commentCrawling( 
			HttpServletRequest request, 
			HttpServletResponse response,
			ModelMap model) throws Exception {

		// 드라이버 설정
		try {
			System.out.println("[PROCESS_INFO] 시스템 환경을 설정하고 있습니다.");
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
			model.addAttribute("systemProp", true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[PROCESS_ERROR] 시스템 환경 설정 중 오류가 발생되었습니다.");
			model.addAttribute("systemProp", false);
		}
		
		String path = "egovframework/story/crawling/naver/blog/comment/crawling";
		LOGGER.info("path : {}", path);
		
		return path;
	}
	
	/**
	 * 댓글 크롤링 처리
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/comment/crawlingProc.*")
	public void crawlingProc( 
			HttpServletRequest request, 
			HttpServletResponse response,
			ModelMap model) throws Exception {
		
		// 실행 로드 시작시간
		long indexActionStartTime = System.currentTimeMillis();
		
		// 세션 초기화
		request.getSession().removeAttribute("RESULT_CONSOLE_FLAG");
		request.getSession().removeAttribute("RESULT_CONSOLE_MSG");
		request.getSession().removeAttribute("COMMENT_LIST");
		request.getSession().removeAttribute("COMMENT_SEARCH_TEXT");
		request.getSession().removeAttribute("COMMENT_BLOGID_OVERLAP");
		
		// 아이디
		String nId = ServletRequestUtils.getStringParameter(request, "nId", "");
		
		// 비밀번호
		String nPw = ServletRequestUtils.getStringParameter(request, "nPw", "");
		
		// 수집을 원하는 대상 url
		String targetUrl = ServletRequestUtils.getStringParameter(request, "targetUrl", "");
		
		// 디버그 여부
		String debug = ServletRequestUtils.getStringParameter(request, "debug", "DEBUG");
		
		// 댓글 내용에 포함되는 단어
		String commentSearchText = ServletRequestUtils.getStringParameter(request, "commentSearchText", "");
		
		// 블로그 아이디 중복 확인
		String blogIdOverlap = ServletRequestUtils.getStringParameter(request, "blogIdOverlap", "Y");
		
		// 최종 댓글 데이터
		List<NaverBlogCommentVO> commentList = new ArrayList<NaverBlogCommentVO>();
		
		// 드라이버 설정
		try {
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 크롤링에 필요한 시스템 환경 설정이 완료 되었습니다.");
			System.out.println("[PROCESS_INFO] 크롤링에 필요한 시스템 환경 설정이 완료 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 크롤링에 필요한 시스템 환경 설정 중 오류가 발생되었습니다.");
			System.out.println("[PROCESS_ERROR] 크롤링에 필요한 시스템 환경 설정 중 오류가 발생되었습니다.");
			throw new RuntimeException(e.getMessage());
		}
		
		// 크롬 설정 및 드라이버 생성
		WebDriver driver = null;
		try {
			//크롬 설정을 담은 객체 생성
			ChromeOptions options = new ChromeOptions();
			
			//브라우저가 눈에 보이지 않고 내부적으로 돈다.
			//설정하지 않을 시 실제 크롬 창이 생성되고, 어떤 순서로 진행되는지 확인할 수 있다.
			if (!debug.equals("DEBUG")) {
				options.addArguments("headless");
			}
			
			//위에서 설정한 옵션은 담은 드라이버 객체 생성
			//옵션을 설정하지 않았을 때에는 생략 가능하다.
			//WebDriver객체가 곧 하나의 브라우저 창이라 생각한다.
			driver = new ChromeDriver(options);
			
			System.out.println("[PROCESS_INFO] 웹 드라이버 설정하고 연결합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 웹 드라이버를 설정하고 연결합니다.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS_ERROR] 웹 드라이버를 연결 및 설정에 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 웹 드라이버를 연결 및 설정에 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		} 
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 대기한다.
		try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}

		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		try {
			// 네이버 로그인
			driver.get("https://nid.naver.com/nidlogin.login?url=http%3A%2F%2Fm.naver.com");
			WebElement inputIdEl = driver.findElement(By.id("id"));
			WebElement inputPwEl = driver.findElement(By.id("pw"));
			System.out.println("[PROCESS_INFO] 네이버 로그인 페이지로 이동하여 입력하신 계정정보로 로그인을 진행합니다..");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 네이버 로그인 페이지로 이동하여 입력하신 계정정보로 로그인을 진행합니다.");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 아이디 처리
			StringSelection stringSelection = new StringSelection(nId);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			Actions builder = new Actions(driver);
			builder.keyDown(inputIdEl, Keys.CONTROL).perform();
			builder.sendKeys(inputIdEl, "v").perform();
			builder.keyUp(inputIdEl, Keys.CONTROL).perform();
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 비밀번호 처리
			stringSelection = new StringSelection(nPw);
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			builder = new Actions(driver);
			builder.keyDown(inputPwEl, Keys.CONTROL).perform();
			builder.sendKeys(inputPwEl, "v").perform();
			builder.keyUp(inputPwEl, Keys.CONTROL).perform();
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 로그인 버튼 클릭
			WebElement loginBtnEl = driver.findElement(By.id("log.login"));
			loginBtnEl.click();
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 기기등록 버튼 클릭
			/*WebElement applyBtnEl = driver.findElement(By.id("new.dontsave"));
			if (applyBtnEl != null) {
				applyBtnEl.click();
				try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			}*/
			
			System.out.println("[PROCESS_INFO] 네이버에 정상적으로 로그인 되었습니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 네이버에 정상적으로 로그인 되었습니다.");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS_ERROR] 네이버 로그인 처리 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 네이버 로그인 처리 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		} 
		
		try {
			// WebDriver을 해당 url로 이동한다.
			driver.get(targetUrl);
			//System.out.println(driver.getPageSource());
			
			// 브라우저 창을 최대화
			//driver.manage().window().maximize();
			
			System.out.println("[PROCESS_INFO] 수집 대상 URL에 접속 되었습니다. 크롤링을 시작합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 대상 URL에 접속 되었습니다. 크롤링을 시작합니다.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS_ERROR] 수집 대상 URL에 접속 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 수집 대상 URL에 접속 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		}
		
		// 브라우저 이동시 생기는 로드시간을 기다린다.
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 대기한다.
		try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
		
		try {
		
			// 블로그의 하단 댓글 전체보기 버튼 클릭 페이지 이동
			WebElement replyBtnEl = driver.findElement(By.cssSelector("div.btn_r > a.btn_reply"));
			js.executeScript("arguments[0].click();", replyBtnEl);
			
			System.out.println("[PROCESS_INFO] 댓글 목록으로 페이지 이동합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 댓글 목록으로 페이지 이동합니다.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS_ERROR] 댓글 목록으로 페이지 이동 중 오류가 발생 되었습니다. 관리자에게 문의하십시오.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 댓글 목록으로 페이지 이동 중 오류가 발생 되었습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		}
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
		try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}

		DecimalFormat df = new DecimalFormat("###,###");
		int num___9TRf 			= 0;
		List<WebElement> el1 	= null;
		try {
			
			// 댓글 전체 갯수 DIV 엘리먼트
			List<WebElement> el = driver.findElements(By.className("num___9TRf"));
			num___9TRf = Integer.parseInt(el.get(0).getText().replace(",", ""));
			System.out.println("[PROCESS_INFO] 크롤링 수집 대상인 댓글의 전체 갯수 : " + df.format(num___9TRf));
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 크롤링 수집 대상인 댓글의 전체 갯수 : " + df.format(num___9TRf));
			
			// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 대기한다.
			Thread.sleep(INTERRUPTED_TIME);
	
			// 네이버 댓글이 모두 펼쳐지도록 스크롤을 진행
			System.out.println("[PROCESS_INFO] 댓글 전체가 활성화 될 수 있도록 스크롤 작업을 시작합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 댓글 전체가 활성화 될 수 있도록 스크롤 작업을 시작합니다.");
			int scrollExecuteNo = 0;
			boolean scroll = true;
			while (scroll) {
				++scrollExecuteNo;
				
				// 최상단으로 스크롤 처리
				js.executeScript("window.scrollTo(0,0)");
				request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 댓글 " + df.format(scrollExecuteNo) + "번 스크롤 처리중...");
	
				// 네이버 댓글 DIV 엘리먼트
				el1 = driver.findElements(By.className("u_cbox_area"));
				
				// 스크롤 해서 불러들일 댓글이 더 있는지 확인
				if (el1.size() >= num___9TRf) {
					scroll = false;
					System.out.println("[PROCESS_INFO] 더 이상 스크롤 할 댓글이 없습니다.");
					request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 더 이상 스크롤 할 댓글이 없습니다.");
				}
				
				// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
				try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS_ERROR] 크롤링 작업 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 크롤링 작업 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		} finally {
			System.out.println("[PROCESS_INFO] 댓글 스크롤 작업을 종료합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 댓글 스크롤 작업을 종료합니다.");
		}
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
		try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
		
		try {
			System.out.println("[PROCESS_INFO] 수집된 댓글 데이터를 추출합니다. 이 작업은 수집된 댓글에 따라 최대 몇 분정도 걸릴 수 있습니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 수집된 댓글 데이터를 추출합니다. 이 작업은 수집된 댓글에 따라 최대 몇 분정도 결릴 수 있습니다.");

			for (int i = 0; i < el1.size(); i++) {
				WebElement wEl = (WebElement) el1.get(i);
				
				String dataResult = "";
				NaverBlogCommentVO commentVO = new NaverBlogCommentVO();
				
				// 비밀댓글인지 확인
				if (wEl.findElements(By.className("u_cbox_info")).size() > 0) {
					
					// 아이디 태그가 존재하는지 확인
					if (wEl.findElements(By.className("u_cbox_name")).size() > 0) {				
						String href = wEl.findElement(By.className("u_cbox_name")).getAttribute("href");
		
						// link url에서 blogId 파라미터 추출
						URL url = new URL(href);
						String query = url.getQuery();
						if (href != null) {
							int pos1 = query.indexOf("?");
							if (pos1 >= 0) query = query.substring(pos1+1);
							
						    String[] params = query.split("&");
						    Map<String, String> map = new HashMap<String, String>();
						    for (String param : params) {
						        String name = param.split("=")[0];
						        String value = param.split("=")[1];
						        map.put(name, value);
						    }
						    
						    dataResult = df.format((i + 1)) + ". 아이디 : " + map.get("blogId");
						    commentVO.setBlogId(map.get("blogId"));
						}
						
						//dataResult = (i+1) + ". 아이디 : " + wEl.findElement(By.className("u_cbox_name")).getAttribute("href");
						//System.out.println("아이디 : " + wEl.findElement(By.className("u_cbox_name")).getLocation());	
					}
					else {
						System.out.println("[PROCESS_ERROR] 아이디 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 아이디 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						
						commentVO.setBlogId("아이디 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
					}
					
					// 닉네임 태그가 존재하는지 확인
					if (wEl.findElements(By.className("u_cbox_nick")).size() > 0) {
						dataResult += ", 닉네임 : " + wEl.findElement(By.className("u_cbox_nick")).getText();
						commentVO.setNickName(wEl.findElement(By.className("u_cbox_nick")).getText());
					}
					else {
						System.out.println("[PROCESS_ERROR] 닉네임 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 닉네임 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						
						commentVO.setNickName("닉네임 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
					}
					
					// 내용 태그가 존재하는지 확인
					if (wEl.findElements(By.className("u_cbox_contents")).size() > 0) {
						dataResult += ", 내용 : " + wEl.findElement(By.className("u_cbox_contents")).getText();
						commentVO.setContents(wEl.findElement(By.className("u_cbox_contents")).getText());	
					}
					else {
						System.out.println("[PROCESS_ERROR] 내용 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 내용 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
						
						commentVO.setContents("내용 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");	
					} 
				}
				else {
					dataResult = i + ". 비밀댓글은 데이터를 추출할수 없습니다.";
					request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR]" + df.format(i) + ". 비밀댓글은 데이터를 추출할수 없습니다.");
					
					commentVO.setContents(dataResult);	
				}
				
				commentList.add(commentVO);
				
				System.out.println(dataResult);	
				System.out.println();
				request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] " + df.format(i) + " / " + df.format(num___9TRf) + " 번째 댓글 추출중...");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("[PROCESS_ERROR] 데이터 추출 중 오류가 발생되었습니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 데이터 추출 중 오류가 발생되었습니다.");
			throw new RuntimeException(e.getMessage());
		} finally {
			long indexActionEndTime = System.currentTimeMillis();
			
			System.out.println("[PROCESS_INFO] " + df.format(num___9TRf) + "건의 댓글 데이터를 추출이 완료되었습니다. 크롤링 소요 시간 : " + ((indexActionEndTime - indexActionStartTime) / 1000.0D));
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] " + df.format(num___9TRf) + "건의 댓글 데이터를 추출이 완료되었습니다. 크롤링 소요 시간 : " + ((indexActionEndTime - indexActionStartTime) / 1000.0D));
		}
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
		try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
		
		try {
			if(driver != null) {
				
				//드라이버 연결 해제
				driver.close(); 
				
				//프로세스 종료
				driver.quit();
			}
		} catch (Exception e) {
			System.out.println("[PROCESS_ERROR] 드라이버 연결 해제 및 프로세스 종료 중 오류가 발생되었습니다..");
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 드라이버 연결 해제 및 프로세스 종료 중 오류가 발생되었습니다.");
			throw new RuntimeException(e.getMessage());
		} finally {
			System.out.println("[PROCESS_INFO] 드라이버 연결을 해제하고 프로세스를 종료합니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 드라이버 연결을 해제하고 프로세스를 종료합니다.");
		}
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
		try {Thread.sleep((INTERRUPTED_TIME));} catch (InterruptedException e) {}
		
		try {

			// 댓글 내용에 포함되는 단어 세션 처리
			request.getSession().setAttribute("COMMENT_SEARCH_TEXT", commentSearchText);
			
			// 블로그 아이디 중복 표시 여부
			request.getSession().setAttribute("COMMENT_BLOGID_OVERLAP", blogIdOverlap);
			
			// 수집된 댓글 데이터 세션 처리 
			request.getSession().setAttribute("COMMENT_LIST", commentList);
		} catch(Exception e) {			
			System.out.println(e.getMessage());
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			System.out.println("[PROCESS_ERROR] 엑셀파일로 변환 중 오류가 발생되었습니다. 관리자에게 문의하세요.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 엑셀파일로 변환 중 오류가 발생되었습니다. 관리자에게 문의하세요.");
			throw new RuntimeException(e.getMessage());
		} finally {
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "end");
			System.out.println("[PROCESS_INFO] 추출된 댓글 데이터를 엑셀파일로 변환중입니다. 변환이 완료되면 자동으로 엑셀파일이 다운로드 됩니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 추출된 댓글 데이터를 엑셀파일로 변환중입니다. 변환이 완료되면 자동으로 엑셀파일이 다운로드 됩니다.");
		}
		
		// HTTP응답속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
		try {Thread.sleep((INTERRUPTED_TIME));} catch (InterruptedException e) {}
	}
    
	/**
	 * 엑셀 다운로드
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/comment/naverBlogCommentExcelDown.do")
	public String naverBlogCommentExcelDown(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "naverBlogCommentExcelDown";
	}
	
	/**
	 * 콘솔 메세지 처리
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/comment/resultConsoleMsg.*")
	public void resultConsoleMsg( 
			HttpServletRequest request, 
			HttpServletResponse response,
			ModelMap model) throws Exception {
		
		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out = response.getWriter();		
		JSONObject jSONObject = new JSONObject();
		
		try {
			String RESULT_CONSOLE_FLAG = (request.getSession().getAttribute("RESULT_CONSOLE_FLAG") == null) ? "" : (String) request.getSession().getAttribute("RESULT_CONSOLE_FLAG");
			String RESULT_CONSOLE_MSG = (request.getSession().getAttribute("RESULT_CONSOLE_MSG") == null) ? "" : (String) request.getSession().getAttribute("RESULT_CONSOLE_MSG");
			jSONObject.put("flag", RESULT_CONSOLE_FLAG);
			jSONObject.put("msg", RESULT_CONSOLE_MSG);
			out.println(jSONObject.toString());			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			jSONObject.put("flag", "false");
			jSONObject.put("msg", "");
			out.println(jSONObject.toString());
		}
	}
}
