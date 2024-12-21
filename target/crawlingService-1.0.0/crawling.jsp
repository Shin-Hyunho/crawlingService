<%@page import="org.openqa.selenium.JavascriptExecutor"%>
<%@page import="org.openqa.selenium.WebElement"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.openqa.selenium.chrome.ChromeDriver"%>
<%@page import="org.openqa.selenium.chrome.ChromeOptions"%>
<%@page import="org.openqa.selenium.WebDriver"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String WEB_DRIVER_ID 	= "webdriver.chrome.driver";
String WEB_DRIVER_PATH = "D:\\Dev\\workspace-egov3.7\\naverCrawling\\src\\main\\webapp\\WEB-INF\\lib\\chromedriver.exe";

try {
	System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
	request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 크롤링에 필요한 시스템 환경 설정이 완료 되었습니다.");
	System.out.println("[PROCESS_INFO] 크롤링에 필요한 시스템 환경 설정이 완료 되었습니다.");
} catch (Exception e) {
	e.printStackTrace();
	request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 크롤링에 필요한 시스템 환경 설정 중 오류가 발생되었습니다.");
	System.out.println("[PROCESS_ERROR] 크롤링에 필요한 시스템 환경 설정 중 오류가 발생되었습니다.");
}

WebDriver driver = null;
try {
	ChromeOptions options = new ChromeOptions();
	options.addArguments("headless");
	
	driver = new ChromeDriver(options);

	System.out.println("[PROCESS_INFO] 웹 드라이버 설정하고 연결합니다.");
} catch(Exception e) {
	System.out.println(e.getMessage());
	System.out.println("[PROCESS_ERROR] 웹 드라이버를 연결 및 설정에 오류가 발생하였습니다. 관리자에게 문의하십시오.");
} 

try {
	driver.get(targetUrl);
	System.out.println("[PROCESS_INFO] 대상 URL에 접속 되었습니다. 크롤링을 시작합니다.");
} catch(Exception e) {
	System.out.println(e.getMessage());
	System.out.println("[PROCESS_ERROR] 대상 URL에 접속 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
}

DecimalFormat df = new DecimalFormat("###,###");
int num___9TRf 			= 0;
List<WebElement> el1 	= null;
try {
	JavascriptExecutor js = (JavascriptExecutor) driver;
	driver.manage().window().maximize();
	
	List<WebElement> el = driver.findElements(By.className("num___9TRf"));
	num___9TRf = Integer.parseInt(el.get(0).getText().replace(",", ""));
	System.out.println("[PROCESS_INFO] 크롤링 수집 대상인 댓글의 전체 갯수 : " + df.format(num___9TRf));
	System.out.println("[PROCESS_INFO] 댓글 전체가 활성화 될 수 있도록 스크롤 작업을 시작합니다.");
	
	int scrollExecuteNo = 0;
	boolean scroll = true;
	while (scroll) {
		++scrollExecuteNo;
		
		js.executeScript("window.scrollTo(0,0)");
		request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 댓글 " + df.format(scrollExecuteNo) + "번 스크롤 처리중...");
		el1 = driver.findElements(By.className("u_cbox_area"));

		if (el1.size() >= num___9TRf) {
			scroll = false;
			System.out.println("[PROCESS_INFO] 더 이상 스크롤 할 댓글이 없습니다.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 더 이상 스크롤 할 댓글이 없습니다.");
		}
		
		Thread.sleep((INTERRUPTED_TIME));
	}
} catch (Exception e) {
	System.out.println(e.getMessage());
	System.out.println("[PROCESS_ERROR] 크롤링 작업 중 오류가 발생하였습니다. 관리자에게 문의하십시오.");
} finally {
	System.out.println("[PROCESS_INFO] 댓글 스크롤 작업을 종료합니다.");
}

try {
	System.out.println("[PROCESS_INFO] 수집된 댓글 데이터를 추출합니다. 이 작업은 수집된 댓글에 따라 최대 몇 분정도 걸릴 수 있습니다.");

	for (int i = 0; i < el1.size(); i++) {
		WebElement wEl = (WebElement) el1.get(i);
		
		String dataResult = "";
		NaverBlogCommentVO commentVO = new NaverBlogCommentVO();
		
		if (wEl.findElements(By.className("u_cbox_info")).size() > 0) {
			if (wEl.findElements(By.className("u_cbox_name")).size() > 0) {				
				String href = wEl.findElement(By.className("u_cbox_name")).getAttribute("href");

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
			}
			else {
				System.out.println("[PROCESS_ERROR] 아이디 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
				commentVO.setBlogId("아이디 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
			}
			
			if (wEl.findElements(By.className("u_cbox_nick")).size() > 0) {
				dataResult += ", 닉네임 : " + wEl.findElement(By.className("u_cbox_nick")).getText();
				commentVO.setNickName(wEl.findElement(By.className("u_cbox_nick")).getText());
			}
			else {
				System.out.println("[PROCESS_ERROR] 닉네임 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
				commentVO.setNickName("닉네임 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
			}
			
			if (wEl.findElements(By.className("u_cbox_contents")).size() > 0) {
				dataResult += ", 내용 : " + wEl.findElement(By.className("u_cbox_contents")).getText();
				commentVO.setContents(wEl.findElement(By.className("u_cbox_contents")).getText());	
			}
			else {
				System.out.println("[PROCESS_ERROR] 내용 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");
				commentVO.setContents("내용 태그를 찾을수 없습니다. 해당 댓글은 비밀 댓글인지 확인하십시오.");	
			} 
		}
		else {
			dataResult = i + ". 비밀댓글은 데이터를 추출할수 없습니다.";
			commentVO.setContents(dataResult);	
		}
		commentList.add(commentVO);
		
		System.out.println(dataResult);	
		System.out.println();
	}
	
} catch(Exception e) {
	e.printStackTrace();
	System.out.println("[PROCESS_ERROR] 데이터 추출 중 오류가 발생되었습니다.");
} finally {
	long indexActionEndTime = System.currentTimeMillis();
	System.out.println("[PROCESS_INFO] " + df.format(num___9TRf) + "건의 댓글 데이터를 추출이 완료되었습니다. 크롤링 소요 시간 : " + ((indexActionEndTime - indexActionStartTime) / 1000.0D));
}

try {
	if(driver != null) {
		driver.close(); 
		driver.quit();
	}
} catch (Exception e) {
	
} finally {
	System.out.println("[PROCESS_INFO] 드라이버 연결을 해제하고 프로세스를 종료합니다.");
	request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_INFO] 드라이버 연결을 해제하고 프로세스를 종료합니다.");
}
%>