package egovframework.crawling.camp.web;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.crawling.camp.common.AutoCampUtil;
import egovframework.crawling.camp.common.Constant;
import egovframework.crawling.camp.service.JungguAutoCampVO;
import egovframework.crawling.camp.service.UljuAutoCampVO;

/**
 * 중구 오토캠핑장 크롤링
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
@RequestMapping("/autoCamp/resv")
public class JungguCampController {
	private static final String TAG 	= "JungguCampController";
	private static final Logger LOGGER 	= LoggerFactory.getLogger(JungguCampController.class);
	
	/**
	 * 중구 오토캠핑장 크롤링
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/jungguCrawling.*")
	public void jungguCrawling(
			@ModelAttribute("jungguAutoCampVO") JungguAutoCampVO searchVO, 
			@RequestParam(value="campPlaceList[]", required = false) List<String> campPlaceList,
			HttpServletRequest req, 
			HttpServletResponse res, 
			ModelMap model) throws IOException, JSONException	{
		
		res.setContentType("text/plain; charset=utf-8");
		PrintWriter out = res.getWriter();
		JSONObject jSONObject = new JSONObject();

		// 드라이브 연결
		WebDriver driver = AutoCampUtil.createWebDriver(Constant.WEB_DRIVER_ID, Constant.WEB_DRIVER_PATH, searchVO.getDebug());
		
		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		// 최종 데이터 결과
		StringBuffer result = new StringBuffer();
		try {

			if (campPlaceList.contains("1")) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1010300");
				searchVO.setPageNm("입화산 제2오토캠핑장");
				result.append(jungguCrawlingProc(driver, searchVO, campPlaceList));
			}
			if (campPlaceList.contains("2")) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1020300");
				searchVO.setPageNm("황방산 오토캠핑장");
				result.append(jungguCrawlingProc(driver, searchVO, campPlaceList));
			}
			if (campPlaceList.contains("3")) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1030300");
				searchVO.setPageNm("태화연 오토캠핑장");
				result.append(jungguCrawlingProc(driver, searchVO, campPlaceList));
			}
			if (campPlaceList.contains("4")) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2030300");
				searchVO.setPageNm("입화산 자연휴양림(야영장)");
				result.append(jungguCrawlingProc(driver, searchVO, campPlaceList));
			}
			if (campPlaceList.contains("5")) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2010400");
				searchVO.setPageNm("입화산 자연휴양림(별뜨락 카라반)");
				result.append(jungguCrawlingProc(driver, searchVO, campPlaceList));
			}
			
			System.out.println("예약 확인이 완료되었습니다.");
			jSONObject.put("flag", true);
			jSONObject.put("msg", "예약 확인이 완료되었습니다.");
			jSONObject.put("result", result.toString());
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("[PROCESS ERROR] 예약 확인 중 오류가 발생되었습니다.");
			jSONObject.put("flag", false);
			jSONObject.put("msg", "[PROCESS ERROR] 예약 확인 중 오류가 발생되었습니다.");
			jSONObject.put("result", "");
			throw new RuntimeException(e.getMessage());
		} finally {
			if(driver != null) {
				System.out.println("크롤링 작업이 완료되어 종료합니다.");
				driver.close(); 
				driver.quit();
			}
		}
		
		out.println(jSONObject.toString());
	}
		
	/**
	 * 중구 오토캠핑장 크롤링 데이터 처리
	 * @param driver
	 * @param pageUrl
	 * @return
	 * @throws Exception
	 */
	public StringBuffer jungguCrawlingProc(
			WebDriver driver, 
			@ModelAttribute("jungguAutoCampVO") JungguAutoCampVO searchVO,
			List<String> campPlaceList) throws Exception {
		
		DecimalFormat moneyFt = new DecimalFormat("###,###");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		// 최종 데이터 결과
		StringBuffer result = new StringBuffer();

		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		try {
			
			// 로그인 처리
			/*if (!"".equals(searchVO.getnId()) && !"".equals(searchVO.getnPw())) {
			
				// 로그인 페이지
				driver.get("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=5010000");
				
				// 아이디 처리
				WebElement inputIdEl = driver.findElement(By.id("chkUserId"));
				StringSelection stringSelection = new StringSelection(searchVO.getnId());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				Actions builder = new Actions(driver);
				builder.keyDown(inputIdEl, Keys.CONTROL).perform();
				builder.sendKeys(inputIdEl, "v").perform();
				builder.keyUp(inputIdEl, Keys.CONTROL).perform();
				
				// 비밀번호 처리
				WebElement inputPwEl = driver.findElement(By.id("chkPasswd"));
				stringSelection = new StringSelection(searchVO.getnPw());
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				builder = new Actions(driver);
				builder.keyDown(inputPwEl, Keys.CONTROL).perform();
				builder.sendKeys(inputPwEl, "v").perform();
				builder.keyUp(inputPwEl, Keys.CONTROL).perform();
	
				// 로그인 버튼 클릭
				WebElement loginBtnEl = driver.findElement(By.id("login_btn"));
				loginBtnEl.click();
			}*/
		      
			// 예약페이지 연결
			driver.get(searchVO.getPageUrl());
			
			// 페이지가 정상적으로 로드가 완료 될때까지 대기
			AutoCampUtil.waitForPageLoad(driver, searchVO.getPageUrl());
			
			// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
			AutoCampUtil.waitForJavascriptLoad(driver);
			System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");

			// 캠핑장 장소 목록
			List<WebElement> site_place = driver.findElements(By.cssSelector(".place > ul > li > a"));
			List<String> site_placeUrl = new ArrayList<String>();
			for (int i=0;i<site_place.size();i++) {
				site_placeUrl.add(site_place.get(i).getAttribute("href"));
			}
			System.out.println(searchVO.getPageNm() + "의 캠핑장 장소가 추출 완료되었습니다.");
			
			// 연박확인
			if ("Y".equals(searchVO.getConNights())) {
	
				// 예약일자 1일후 일자
		        Date date = sdf.parse(searchVO.getResvDate());
		        Calendar cal = Calendar.getInstance();
		        cal.setTime(date);
		        cal.add(Calendar.DATE, 1);
		        String resvDateAfter1 = sdf.format(cal.getTime());
		        
		        // 예약일자, 1일후 일자
		        String[] resvDateArr = {searchVO.getResvDate(), resvDateAfter1};
				
				// 사이트 장소별로 순차 확인
				for (int j=0;j<site_placeUrl.size();j++) {
					
					// 변경된 페이지로 드라이브 재 연결
					driver.get(site_placeUrl.get(j));
					
					// 사이트 장소 목록
					site_place = driver.findElements(By.cssSelector(".place > ul > li > a"));
					
					// 사이트 종류 확인 버튼 클릭
					js.executeScript("$('.place > .btn_place').click();");
					
					// 사이트 목록 클릭 전환
					WebElement site_placeEl = (WebElement) site_place.get(j);
					js.executeScript("arguments[0].click();", site_placeEl);
					
					// 현재 사이트 장소명
					String site_placeNm = driver.findElement(By.cssSelector(".place > .btn_place")).getText();
					
					// 연박 오픈 체크 목록
					List<String> resvOpenList = new ArrayList<String>();
					
					// 1박 예약 상태 목록
					Map<String, String[]> preResvInfo = new HashMap<String, String[]>();
					
					// 연박 일자별 확인
					for (int h=0;h<resvDateArr.length;h++) {
						
						// 달력 readonly 속성 제거 후 예약일자 value 설정
						js.executeScript("$('#calPick').removeAttr('readonly').val('" + resvDateArr[h] + "');");
						System.out.println("예약 날짜 선택 : " + resvDateArr[h]);
						
						// 필수 함수 실행
						js.executeScript("fAjaxSiteList('" + resvDateArr[h] + "');");

						// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
						(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".site_list ul > li")));
						//AutoCampUtil.isWaitElementPooling(driver, ".site_list ul > li", 30);
						
						// 데이터 결과 처리
						List<WebElement> site_list = driver.findElements(By.cssSelector(".site_list ul > li"));
						
						// 1박 예약 상태 확인
						if (h == 0) {
							for (int i=0;i<site_list.size();i++) {
								WebElement wEl = (WebElement) site_list.get(i);
								
								wEl.getAttribute("id");
								
								// 예약이 비어진 상태 체크
								if (!"done".equals(wEl.getAttribute("class"))) {
									resvOpenList.add(driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim());
									System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + ", 예약상태 : " + wEl.findElement(By.className("stat")).getText());
								}
								else {
									System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + ", 예약상태 : " + wEl.findElement(By.className("stat")).getText());
								}
								
								// 예약 클릭 이벤트 함수 
								String fAjaxSiteSelect = wEl.getAttribute("id").trim() + "|" + resvDateArr[0] + "|L0|" + wEl.findElement(By.className("name")).getText();
								
								// 사이트별 1박 예약 상태
								String[] resvInfo = {wEl.findElement(By.className("name")).getText().split("-")[0].trim(), wEl.findElement(By.className("fair")).getText(), wEl.findElement(By.className("stat")).getText(), fAjaxSiteSelect};
								preResvInfo.put(driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim(), resvInfo);
							}
						}
						else {
							// 2박 예약 상태 확인
							for (int i=0;i<site_list.size();i++) {
								WebElement wEl = (WebElement) site_list.get(i);
								
								if (i == 0) {
									result.append("<h1><small>" + site_placeNm + "</small></h1>");									
									result.append("<table class=\"table table-striped\">");
									result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
									result.append(	"<thead>");
									result.append(		"<tr>");
									result.append(			"<th>예약일자</th>");
									result.append(			"<th>사이트</th>");
									result.append(			"<th>예약금액</th>");
									result.append(			"<th>예약상태</th>");
									result.append(			"<th>예약실행</th>");
									result.append(		"</tr>");
									result.append(	"</thead>");
									result.append(	"<tbody>");
								}
								
								if (wEl.findElements(By.className("stat")).size() > 0) {
									System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + ", 예약상태 : " + wEl.findElement(By.className("stat")).getText());
									
									// 1박 예약 금액
									String preMoney = "0";
									if (preResvInfo.get( driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim() ) != null) {
										preMoney = preResvInfo.get( driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim() )[1];
									}
									
									// 최종 금액 합산
									int resvTotalMoney = Integer.parseInt( preMoney.replaceAll("[^0-9]", "") ) 
											+ Integer.parseInt( (wEl.findElement(By.className("fair")).getText()).replaceAll("[^0-9]", "") );
									
									// 예약 확인 출력
									boolean printYn = false;
									if ("Y".equals(searchVO.getPrintAllMode())) {
										printYn = true;
									}
									else if ("N".equals(searchVO.getPrintAllMode())) {
										if (!"done".equals(wEl.getAttribute("class")) 
												&& resvOpenList.contains( (driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim()))) {
											printYn = true;
										}
									}
									
									if (printYn) {
										result.append("<tr>");
										result.append(	"<td>" + resvDateArr[0] + " ~ " + resvDateArr[1] + "</td>");
										result.append(	"<td>" + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + "</td>");
										result.append(	"<td>" + moneyFt.format(resvTotalMoney) + "원 (" + preMoney + " + " + "" + wEl.findElement(By.className("fair")).getText() + ")</td>");
										result.append(	"<td>");
										
										String resvBtn = "";
										String preStat = preResvInfo.get( driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim() )[2];
										if ( "예약하기".equals(preStat) && "예약하기".equals(wEl.findElement(By.className("stat")).getText()) ) {
											result.append("<span class=\"label label-success\">예약하기</span>");
										}
										else if ( "예약완료".equals(preStat) && "예약완료".equals(wEl.findElement(By.className("stat")).getText()) ) {
											result.append("<span class=\"label label-default\">예약완료</span>");
										}
										else if ( "예약불가".equals(preStat) && "예약불가".equals(wEl.findElement(By.className("stat")).getText()) ) {
											result.append("<span class=\"label label-danger\">예약불가</span>");
										}
										else {
											result.append("<span class=\"label label-danger\">연박불가 (" + preStat + ", " + wEl.findElement(By.className("stat")).getText() + ")</span>");
										}
										
										result.append(	"</td>");
										
										// 예약 클릭 이벤트 함수 
										String fAjaxSiteSelect = wEl.getAttribute("id").trim() + "|" + resvDateArr[1] + "|L0|" + wEl.findElement(By.className("name")).getText();

										result.append("		<td>");
										result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[0] + "');jungAutoResv('" + searchVO.getPageNm() + "', '" + preResvInfo.get(driver.findElement(By.cssSelector(".place > .btn_place")).getText() + "-" + wEl.findElement(By.className("name")).getText().split("-")[0].trim())[3] + "');\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[0] + " 예약실행</span></button>");
										result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[1] + "');jungAutoResv('" + searchVO.getPageNm() + "', '" + fAjaxSiteSelect + "');\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[1] + " 예약실행</span></button>");
										result.append("		</td>");
										
										result.append("</tr>");
									}
								}
								
								if (i == (site_list.size()-1)) {
									result.append(	"</tbody>");
									result.append("</table>");
								}
							}	
						}
					}
				}
			}
			else {
				
				// 사이트 장소별로 순차 확인
				for (int j = 0; j < site_placeUrl.size(); j++) {
					
					// 변경된 페이지로 드라이브 재 연결
					driver.get(site_placeUrl.get(j));
					
					// 현재 사이트 장소명
					String site_placeNm = driver.findElement(By.cssSelector(".place > .btn_place")).getText();
					
					// 사이트 장소 목록
					site_place = driver.findElements(By.cssSelector(".place > ul > li > a"));
					
					// 사이트 종류 확인 버튼 클릭
					js.executeScript("$('.place > .btn_place').click();");
					
					// 사이트 목록 클릭 전환
					WebElement site_placeEl = (WebElement) site_place.get(j);
					js.executeScript("arguments[0].click();", site_placeEl);
					
					// 달력 readonly 속성 제거 후 예약일자 value 설정
					js.executeScript("$('#calPick').removeAttr('readonly').val('" + searchVO.getResvDate() + "');");
					System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
					
					// 필수 함수 실행
					js.executeScript("fAjaxSiteList('" + searchVO.getResvDate() + "');");

					// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
					(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".site_list ul > li")));
					//AutoCampUtil.isWaitElementPooling(driver, ".site_list ul > li", 30);
					
					// 데이터 결과 처리
					List<WebElement> site_list = driver.findElements(By.cssSelector(".site_list ul > li"));
					for (int i = 0; i < site_list.size(); i++) {
						WebElement wEl = (WebElement) site_list.get(i);
						
						if (i == 0) {
							result.append("<h1><small>" + site_placeNm + "</small></h1>");									
							result.append("<table class=\"table table-striped\">");
							result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
							result.append(	"<thead>");
							result.append(		"<tr>");
							result.append(			"<th>예약일자</th>");
							result.append(			"<th>사이트</th>");
							result.append(			"<th>예약금액</th>");
							result.append(			"<th>예약상태</th>");
							result.append(			"<th>예약실행</th>");
							result.append(		"</tr>");
							result.append(	"</thead>");
							result.append(	"<tbody>");
						}
						
						if (wEl.findElements(By.className("stat")).size() > 0) {
							System.out.println("예약일자 : " + searchVO.getResvDate() + ", 사이트 : " + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + ", 예약상태 : " + wEl.findElement(By.className("stat")).getText());

							String resvColor = "label-default";
							String resvBtn = "";
							if ( !"done".equals(wEl.getAttribute("class")) ) {
								resvColor = "label-success";
								
								// 예약 클릭 이벤트 함수 
								String fAjaxSiteSelect = wEl.getAttribute("id").trim() + "|" + searchVO.getResvDate() + "|L0|" + wEl.findElement(By.className("name")).getText();
								resvBtn = "<button type=\"button\" onclick=\"$('#resvDate').val('" + searchVO.getResvDate() + "');jungAutoResv('" + searchVO.getPageNm() + "', '" + fAjaxSiteSelect + "');\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + searchVO.getResvDate() + " 예약실행</span></button>";
							}
							else {
								if ("예약불가".equals(wEl.findElement(By.className("stat")).getText())) {
									resvColor = "label-danger";
								}
							}
							
							// 예약 확인 출력
							boolean printYn = false;
							if ("Y".equals(searchVO.getPrintAllMode())) {
								printYn = true;
							}
							else if ("N".equals(searchVO.getPrintAllMode()) && !"done".equals(wEl.getAttribute("class"))) {
								printYn = true;
							}
							
							if (printYn) {
								result.append("<tr>");
								result.append(	"<td>" + searchVO.getResvDate() + "</td>");
								result.append(	"<td>" + wEl.findElement(By.className("name")).getText().split("-")[0].trim() + "</td>");
								result.append(	"<td>" + wEl.findElement(By.className("fair")).getText() + "</td>");
								result.append(	"<td><span class=\"label " + resvColor + "\">" + wEl.findElement(By.className("stat")).getText() + "</span></td>");
								result.append(	"<td>" + resvBtn + "</td>");
								result.append("</tr>");
							}
						}
						
						if (i == (site_list.size() - 1)) {
							result.append("</tbody>");
							result.append("</table>");
						}
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}

	/**
	 * 오토캠핑장 자동예약 실행
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/jungguResv.*")
	public void jungguResv(
			@ModelAttribute("jungguAutoCampVO") JungguAutoCampVO searchVO, 
			HttpServletRequest req, 
			HttpServletResponse res, 
			ModelMap model) throws IOException, JSONException	{
		
		res.setContentType("text/plain; charset=utf-8");
		PrintWriter out = res.getWriter();
		JSONObject jSONObject = new JSONObject();
		
		// 드라이브 연결
		WebDriver driver = AutoCampUtil.createWebDriver(Constant.WEB_DRIVER_ID, Constant.WEB_DRIVER_PATH, searchVO.getDebug());
		 
		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		// 최종 데이터 결과
		StringBuffer result = new StringBuffer();
		try {

			// 로그인 페이지
			driver.get("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=5010000");
			
			// 아이디 처리
			WebElement inputIdEl = driver.findElement(By.id("chkUserId"));
			StringSelection stringSelection = new StringSelection(searchVO.getnId());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			Actions builder = new Actions(driver);
			builder.keyDown(inputIdEl, Keys.CONTROL).perform();
			builder.sendKeys(inputIdEl, "v").perform();
			builder.keyUp(inputIdEl, Keys.CONTROL).perform();
			
			// 비밀번호 처리
			WebElement inputPwEl = driver.findElement(By.id("chkPasswd"));
			stringSelection = new StringSelection(searchVO.getnPw());
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			builder = new Actions(driver);
			builder.keyDown(inputPwEl, Keys.CONTROL).perform();
			builder.sendKeys(inputPwEl, "v").perform();
			builder.keyUp(inputPwEl, Keys.CONTROL).perform();

			// 로그인 버튼 클릭
			WebElement loginBtnEl = driver.findElement(By.id("login_btn"));
			loginBtnEl.click();

			if ("입화산 제2오토캠핑장".equals(searchVO.getPageNm())) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1010300");
			}
			else if ("황방산 오토캠핑장".equals(searchVO.getPageNm())) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1020300");
			}
			else if ("태화연 오토캠핑장".equals(searchVO.getPageNm())) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1030300");
			}
			else if ("입화산 자연휴양림(야영장)".equals(searchVO.getPageNm())) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2030300");
			}
			else if ("입화산 자연휴양림(별뜨락 카라반)".equals(searchVO.getPageNm())) {
				searchVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2010400");
			}
			
			// 예약 페이지
			driver.get(searchVO.getPageUrl());
			
			// 달력 readonly 속성 제거 후 예약일자 value 설정
			js.executeScript("$('#calPick').removeAttr('readonly').val('" + searchVO.getResvDate() + "');");
			System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
			
			// 필수 함수 실행
			js.executeScript("fAjaxSiteList('" + searchVO.getResvDate() + "');");

			// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".site_list ul > li")));
			
			// 예약하기 버튼 클릭 실행
			String[] fAjaxSiteSelectArr = searchVO.getResvIdx().split("|");
			// onclick="fAjaxSiteSelect(&quot;CP_SITE_000000000137&quot;, &quot;2023-09-12&quot;, &quot;L0&quot;, &quot;오토(대형)506&quot;); return false;"
			js.executeScript("fAjaxSiteSelect(\"" + fAjaxSiteSelectArr[0] + "\", \"" + fAjaxSiteSelectArr[1] + "\", \"" + fAjaxSiteSelectArr[2] + "\", \"" + fAjaxSiteSelectArr[3] + "\");");
						
			jSONObject.put("flag", true);
			jSONObject.put("msg", "자동화 된 프로그램에 의해 최종 예약페이지까지 안내 되었습니다. 각 입력란에 사용자의 정보를 입력하고 예약을 완료 해주십시오!.");
			jSONObject.put("result", result.toString());
		}
		/*catch (UnhandledAlertException f) {
			try {
		        Alert alert = driver.switchTo().alert();
		        String alertText = alert.getText();
		        System.out.println("Alert data: " + f.getMessage());
		        
				jSONObject.put("flag", false);
				jSONObject.put("msg", "[PROCESS ERROR]" + f.getMessage());
				jSONObject.put("result", "");
		        //alert.accept();
		    } catch (NoAlertPresentException e) {
		        e.printStackTrace();
		    }
		}*/
		catch(Exception e) {
			System.out.println("예약 실행 중 오류가 발생되었습니다.");
			System.out.println(e.getMessage());
			jSONObject.put("flag", false);
			jSONObject.put("msg", e.getMessage());
			jSONObject.put("result", "");
			throw new RuntimeException(e.getMessage());
		} finally {
			if(driver != null) {
				//driver.close(); 
				//driver.quit();
			}
		}
		
		out.println(jSONObject.toString());
	}
}
