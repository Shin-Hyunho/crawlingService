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
import egovframework.crawling.camp.service.BukguAutoCampVO;

/**
 * 북구 오토캠핑장 크롤링
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
public class BukguCampController {
	private static final String TAG 	= "BukguCampController";
	private static final Logger LOGGER 	= LoggerFactory.getLogger(BukguCampController.class);
		
	// interrupted time
	private static final int INTERRUPTED_TIME 	= 500;

	/**
	 * 북구 오토캠핑장 크롤링
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/bukguCrawling.*")
	public void bukguCrawling(
			@ModelAttribute("bukguAutoCampVO") BukguAutoCampVO searchVO, 
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
		
			searchVO.setPageUrl("https://camping.ubimc.or.kr/Pmreservation.do");
			searchVO.setPageNm("울산 북구 강동오토캠핑장");
			result.append(bukguCrawlingProc(driver, searchVO, campPlaceList));
			
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
	 * 북구 오토캠핑장 크롤링 데이터 처리
	 * @param driver
	 * @param pageUrl
	 * @return
	 * @throws Exception
	 */
	public StringBuffer bukguCrawlingProc(
			WebDriver driver, 
			@ModelAttribute("bukguAutoCampVO") BukguAutoCampVO searchVO,
			List<String> campPlaceList) throws Exception {
		
		DecimalFormat moneyFt = new DecimalFormat("###,###");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		// 최종 데이터 결과
		StringBuffer result = new StringBuffer();
	
		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		try {
			
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
				
				// 연박 오픈 체크 목록
				List<String> resvOpenList = new ArrayList<String>();
				
				// 1박 예약 상태 목록
				Map<String, String[]> preResvInfo = new HashMap<String, String[]>();
				
				// 연박 일자별 확인
				for (int h=0;h<resvDateArr.length;h++) {

					// 예약페이지 연결
					driver.get(searchVO.getPageUrl());
					
					// 페이지가 정상적으로 로드가 완료 될때까지 대기
					AutoCampUtil.waitForPageLoad(driver, searchVO.getPageUrl());
					
					// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
					AutoCampUtil.waitForJavascriptLoad(driver);
					if (h == 0) System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");
					
					// 날짜 설정
					js.executeScript("$('#resDate').val('" + resvDateArr[h] + "');");
					js.executeScript("$('#date_line').html('선택날짜 :" + resvDateArr[h] + "');");
					js.executeScript("$('.date_line').show();");
					System.out.println("예약 날짜 선택 : " + resvDateArr[h]);
						
					// 첫번째 캠핑장은 선택되어진 상태로 예약목록이 호출되어 지므로 예외처리
					js.executeScript("addTable();");
						
					// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
					(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
					
					// 예약 목록
					List<WebElement> site_list = driver.findElements(By.cssSelector("table#tableSite tbody > tr"));
					
					// 1박 예약 상태 확인
					if (h == 0) {
						for (int i=0;i<site_list.size();i++) {
							WebElement wEl = (WebElement) site_list.get(i);
							
							// 사이트명
							String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
							
							// 예약현황
							String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
							
							// 버튼 이벤트
							String resvBtnEvent = "";
							if ( "예약가능".equals(stat) ) {
								resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
							}
							
							// 예약이 비어진 상태 체크
							if ( "예약가능".equals(stat) ) {
								resvOpenList.add(siteNm);
								System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + siteNm + ", 예약상태 : " + stat);
							}
							else {
								System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + siteNm + ", 예약상태 : " + stat);
							}
							
							// 사이트별 1박 예약 상태
							String[] resvInfo = {siteNm, stat, resvBtnEvent};
							preResvInfo.put(siteNm, resvInfo);
						}
					}
					else {
						// 2박 예약 상태 확인
						for (int i=0;i<site_list.size();i++) {
							WebElement wEl = (WebElement) site_list.get(i);
							
							// 사이트명
							String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
							
							// 예약현황
							String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
							
							// 버튼 이벤트
							String resvBtnEvent = "";
							if ( "예약가능".equals(stat) ) {
								resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
							}
							
							if (i == 0) {
								result.append("<h1><small>울산 북구 강동오토캠핑장</small></h1>");									
								result.append("<table class=\"table table-striped\">");
								result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
								result.append(	"<thead>");
								result.append(		"<tr>");
								result.append(			"<th>예약일자</th>");
								result.append(			"<th>사이트명</th>");
								result.append(			"<th>예약현황</th>");
								result.append(			"<th>예약실행</th>");
								result.append(		"</tr>");
								result.append(	"</thead>");
								result.append(	"<tbody>");
							}
							
							System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트명 : " + siteNm + ", 예약현황 : " + stat);
							
							String resvColor = "label-default";
							if ( "예약가능".equals(stat) ) {
								resvColor = "label-success";
							}
							else if ( "예약종료".equals(stat) ) {
								resvColor = "label-danger";
							}
							
							boolean printYn = false;
							if ( "Y".equals(searchVO.getPrintAllMode()) ) {
								printYn = true;
							}
							else if ("N".equals(searchVO.getPrintAllMode())) {
								if ( "예약가능".equals(stat) && resvOpenList.contains(siteNm) ) {
									printYn = true;
								}
							}
							
							if (printYn) {
								result.append("	<tr>");
								result.append("		<td>" + resvDateArr[0] + " ~ " + resvDateArr[1] + "</td>");
								result.append("		<td>" + siteNm + "</td>");
								result.append("		<td>");

								String preStat = preResvInfo.get( siteNm )[1];
								if ( "예약가능".equals(preStat) && "예약가능".equals(stat) ) {
									result.append("<span class=\"label label-success\">예약가능</span>");
								}
								else if ( "예약종료".equals(preStat) && "예약종료".equals(stat) ) {
									result.append("<span class=\"label label-default\">예약종료</span>");
								}
								else {
									result.append("<span class=\"label label-danger\">연박불가 (" + preStat + ", " + stat + ")</span>");
								}
								
								result.append("		</td>");
								result.append("		<td>");
								if ( !"".equals(preResvInfo.get( siteNm )[2]) ) {
									result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[0] + "');" + preResvInfo.get( siteNm )[2] + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[0] + " 예약실행</span></button>");
								}
								if ( !"".equals(resvBtnEvent) ) {
									result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[1] + "');" + resvBtnEvent + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[1] + "  예약실행</span></button>");
								}
								result.append("		</td>");
								result.append("	</tr>");
							}
							
							if (i == (site_list.size()-1)) {
								result.append(	"</tbody>");
								result.append("</table>");
							}
						}	
					}
				}
			}
			else {

				// 예약페이지 연결
				driver.get(searchVO.getPageUrl());
				
				// 페이지가 정상적으로 로드가 완료 될때까지 대기
				AutoCampUtil.waitForPageLoad(driver, searchVO.getPageUrl());
				
				// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
				AutoCampUtil.waitForJavascriptLoad(driver);	
				System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");
				
				// 캘린더 날짜 선택
				js.executeScript("$('#resDate').val('" + searchVO.getResvDate() + "');");
				js.executeScript("$('#date_line').html('선택날짜 :" + searchVO.getResvDate() + "');");
				js.executeScript("$('.date_line').show();");
				System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
				
				// 예약 목록 실행
				js.executeScript("addTable();");
				
				// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
				(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
				
				// 예약 목록
				List<WebElement> site_list = driver.findElements(By.cssSelector("table#tableSite tbody > tr"));
				for (int i=0;i<site_list.size();i++) {
					WebElement wEl = (WebElement) site_list.get(i);
					
					// 사이트명
					String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
					
					// 예약현황
					String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
					
					// 버튼 이벤트
					String resvBtnEvent = "";
					if ( "예약가능".equals(stat) ) {
						resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
					}
					
					if (i == 0) {
						result.append("<h1><small>울산 북구 강동오토캠핑장</small></h1>");									
						result.append("<table class=\"table table-striped\">");
						result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
						result.append(	"<thead>");
						result.append(		"<tr>");
						result.append(			"<th>예약일자</th>");
						result.append(			"<th>사이트명</th>");
						result.append(			"<th>예약현황</th>");
						result.append(			"<th>예약실행</th>");
						result.append(		"</tr>");
						result.append(	"</thead>");
						result.append(	"<tbody>");
					}
				
					System.out.println("예약일자 : " + searchVO.getResvDate() + ", 사이트명 : " + siteNm + ", 예약현황 : " + stat);
					
					String resvColor = "label-default";
					if ( "예약가능".equals(stat) ) {
						resvColor = "label-success";
					}
					else if ( "예약종료".equals(stat) ) {
						resvColor = "label-danger";
					}
					
					boolean printYn = false;
					if ( "Y".equals(searchVO.getPrintAllMode()) ) {
						printYn = true;
					}
					else if ( "N".equals(searchVO.getPrintAllMode()) && "예약가능".equals(stat) ) {
						printYn = true;
					}
					
					if (printYn) {
						result.append("	<tr>");
						result.append("		<td>" + searchVO.getResvDate() + "</td>");
						result.append("		<td>" + siteNm + "</td>");
						result.append("		<td><span class=\"label " + resvColor + "\">" + stat + "</span></td>");
						result.append("		<td>");
						if ( !"".equals(resvBtnEvent) ) {
							result.append("		<button type=\"button\" onclick=\"" + resvBtnEvent + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + searchVO.getResvDate() + " 예약실행</span></button>");
						}
						result.append("		</td>");
						result.append("	</tr>");
					}
					
					if (i == (site_list.size() - 1)) {
						result.append("</tbody>");
						result.append("</table>");
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}

	/**
	 * 북구 오토캠핑장 자동예약 실행
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/bukguResv.*")
	public void bukguResv(
			@ModelAttribute("dongguAutoCampVO") BukguAutoCampVO searchVO, 
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
			driver.get("https://camping.ubimc.or.kr/login.do");

			// 통합회원 로그인 버튼 클릭
			WebElement loginBtnEl = driver.findElement(By.id("login_ulsan"));
			loginBtnEl.click();
			
			// 아이디 처리
			WebElement inputIdEl = driver.findElement(By.name("userid"));
			StringSelection stringSelection = new StringSelection(searchVO.getnId());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			Actions builder = new Actions(driver);
			builder.keyDown(inputIdEl, Keys.CONTROL).perform();
			builder.sendKeys(inputIdEl, "v").perform();
			builder.keyUp(inputIdEl, Keys.CONTROL).perform();
			
			// 비밀번호 처리
			WebElement inputPwEl = driver.findElement(By.name("password"));
			stringSelection = new StringSelection(searchVO.getnPw());
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			builder = new Actions(driver);
			builder.keyDown(inputPwEl, Keys.CONTROL).perform();
			builder.sendKeys(inputPwEl, "v").perform();
			builder.keyUp(inputPwEl, Keys.CONTROL).perform();

			// 로그인 버튼 클릭
			loginBtnEl = driver.findElement(By.cssSelector(".form-login > input"));
			loginBtnEl.click();
			
			// 예약 페이지
			driver.get("https://camping.ubimc.or.kr/Pmreservation.do");

			// 캘린더 날짜 선택
			js.executeScript("$('#resDate').val('" + searchVO.getResvDate() + "');");
			js.executeScript("$('#date_line').html('선택날짜 :" + searchVO.getResvDate() + "');");
			js.executeScript("$('.date_line').show();");
			System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
			
			// 예약 목록 실행
			js.executeScript("addTable();");
			
			// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
			
			/*
			// 예약 신청 클릭 실행
			js.executeScript("subIdx('" + searchVO.getResvIdx() + "');");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}

			// 예약하기 버튼 클릭
			WebElement resvBtnEl = driver.findElement(By.cssSelector(".bttns > button"));
			resvBtnEl.click();
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 이용안내 팝업 확인
			js.executeScript("fnNextStep2();");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			*/
			
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

	/**
	 * 당사 오토캠핑장 크롤링
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/dangsaCrawling.*")
	public void dangsaCrawling(
			@ModelAttribute("bukguAutoCampVO") BukguAutoCampVO searchVO, 
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
			searchVO.setPageUrl("https://camping.ubimc.or.kr/Pmreservation.do");
			searchVO.setPageNm("울산 북구 당사현대차오션캠프");
			result.append(dangsaCrawlingProc(driver, searchVO, campPlaceList));
			
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
	 * 당사 오토캠핑장 크롤링 데이터 처리
	 * @param driver
	 * @param pageUrl
	 * @return
	 * @throws Exception
	 */
	public StringBuffer dangsaCrawlingProc(
			WebDriver driver, 
			@ModelAttribute("bukguAutoCampVO") BukguAutoCampVO searchVO,
			List<String> campPlaceList) throws Exception {
		
		DecimalFormat moneyFt = new DecimalFormat("###,###");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		// 최종 데이터 결과
		StringBuffer result = new StringBuffer();
	
		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		try {
			
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
				
				// 연박 오픈 체크 목록
				List<String> resvOpenList = new ArrayList<String>();
				
				// 1박 예약 상태 목록
				Map<String, String[]> preResvInfo = new HashMap<String, String[]>();
				
				// 연박 일자별 확인
				for (int h=0;h<resvDateArr.length;h++) {

					// 예약페이지 연결
					driver.get(searchVO.getPageUrl());
					
					// 페이지가 정상적으로 로드가 완료 될때까지 대기
					AutoCampUtil.waitForPageLoad(driver, searchVO.getPageUrl());

					// 당사현대차오션캠프 전환 버튼 클릭
					WebElement campingBtnEl = driver.findElement(By.id("btn_siteKindCode_010200"));
					campingBtnEl.click();
					
					// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
					AutoCampUtil.waitForJavascriptLoad(driver);
					if (h == 0) System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");
					
					// 날짜 설정
					js.executeScript("$('#resDate').val('" + resvDateArr[h] + "');");
					js.executeScript("$('#date_line').html('선택날짜 :" + resvDateArr[h] + "');");
					js.executeScript("$('.date_line').show();");
					System.out.println("예약 날짜 선택 : " + resvDateArr[h]);
						
					// 첫번째 캠핑장은 선택되어진 상태로 예약목록이 호출되어 지므로 예외처리
					js.executeScript("addTable();");
						
					// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
					(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
					
					// 예약 목록
					List<WebElement> site_list = driver.findElements(By.cssSelector("table#tableSite tbody > tr"));
					
					// 1박 예약 상태 확인
					if (h == 0) {
						for (int i=0;i<site_list.size();i++) {
							WebElement wEl = (WebElement) site_list.get(i);
							
							// 사이트명
							String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
							
							// 예약현황
							String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
							
							// 버튼 이벤트
							String resvBtnEvent = "";
							if ( "예약가능".equals(stat) ) {
								resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
							}
							
							// 예약이 비어진 상태 체크
							if ( "예약가능".equals(stat) ) {
								resvOpenList.add(siteNm);
								System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + siteNm + ", 예약상태 : " + stat);
							}
							else {
								System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트 : " + siteNm + ", 예약상태 : " + stat);
							}
							
							// 사이트별 1박 예약 상태
							String[] resvInfo = {siteNm, stat, resvBtnEvent};
							preResvInfo.put(siteNm, resvInfo);
						}
					}
					else {
						// 2박 예약 상태 확인
						for (int i=0;i<site_list.size();i++) {
							WebElement wEl = (WebElement) site_list.get(i);
							
							// 사이트명
							String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
							
							// 예약현황
							String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
							
							// 버튼 이벤트
							String resvBtnEvent = "";
							if ( "예약가능".equals(stat) ) {
								resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
							}
							
							if (i == 0) {
								result.append("<h1><small>울산 북구 당사오션 오토캠핑장</small></h1>");									
								result.append("<table class=\"table table-striped\">");
								result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
								result.append(	"<thead>");
								result.append(		"<tr>");
								result.append(			"<th>예약일자</th>");
								result.append(			"<th>사이트명</th>");
								result.append(			"<th>예약현황</th>");
								result.append(			"<th>예약실행</th>");
								result.append(		"</tr>");
								result.append(	"</thead>");
								result.append(	"<tbody>");
							}
							
							System.out.println("예약일자 : " + resvDateArr[h] + ", 사이트명 : " + siteNm + ", 예약현황 : " + stat);
							
							String resvColor = "label-default";
							if ( "예약가능".equals(stat) ) {
								resvColor = "label-success";
							}
							else if ( "예약종료".equals(stat) ) {
								resvColor = "label-danger";
							}
							
							boolean printYn = false;
							if ( "Y".equals(searchVO.getPrintAllMode()) ) {
								printYn = true;
							}
							else if ("N".equals(searchVO.getPrintAllMode())) {
								if ( "예약가능".equals(stat) && resvOpenList.contains(siteNm) ) {
									printYn = true;
								}
							}
							
							if (printYn) {
								result.append("	<tr>");
								result.append("		<td>" + resvDateArr[0] + " ~ " + resvDateArr[1] + "</td>");
								result.append("		<td>" + siteNm + "</td>");
								result.append("		<td>");

								String preStat = preResvInfo.get( siteNm )[1];
								if ( "예약가능".equals(preStat) && "예약가능".equals(stat) ) {
									result.append("<span class=\"label label-success\">예약가능</span>");
								}
								else if ( "예약종료".equals(preStat) && "예약종료".equals(stat) ) {
									result.append("<span class=\"label label-default\">예약종료</span>");
								}
								else {
									result.append("<span class=\"label label-danger\">연박불가 (" + preStat + ", " + stat + ")</span>");
								}
								
								result.append("		</td>");
								result.append("		<td>");
								if ( !"".equals(preResvInfo.get( siteNm )[2]) ) {
									result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[0] + "');" + preResvInfo.get( siteNm )[2] + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[0] + " 예약실행</span></button>");
								}
								if ( !"".equals(resvBtnEvent) ) {
									result.append("		<button type=\"button\" onclick=\"$('#resvDate').val('" + resvDateArr[1] + "');" + resvBtnEvent + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + resvDateArr[1] + "  예약실행</span></button>");
								}
								result.append("		</td>");
								result.append("	</tr>");
							}
							
							if (i == (site_list.size()-1)) {
								result.append(	"</tbody>");
								result.append("</table>");
							}
						}	
					}
				}
			}
			else {

				// 예약페이지 연결
				driver.get(searchVO.getPageUrl());
				
				// 페이지가 정상적으로 로드가 완료 될때까지 대기
				AutoCampUtil.waitForPageLoad(driver, searchVO.getPageUrl());

				// 당사현대차오션캠프 전환 버튼 클릭
				WebElement campingBtnEl = driver.findElement(By.id("btn_siteKindCode_010200"));
				campingBtnEl.click();
				
				// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
				AutoCampUtil.waitForJavascriptLoad(driver);	
				System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");
				
				// 캘린더 날짜 선택
				js.executeScript("$('#resDate').val('" + searchVO.getResvDate() + "');");
				js.executeScript("$('#date_line').html('선택날짜 :" + searchVO.getResvDate() + "');");
				js.executeScript("$('.date_line').show();");
				System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
				
				// 예약 목록 실행
				js.executeScript("addTable();");
				
				// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
				(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
				
				// 예약 목록
				List<WebElement> site_list = driver.findElements(By.cssSelector("table#tableSite tbody > tr"));
				for (int i=0;i<site_list.size();i++) {
					WebElement wEl = (WebElement) site_list.get(i);
					
					// 사이트명
					String siteNm = wEl.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
					
					// 예약현황
					String stat = wEl.findElement(By.cssSelector("td:nth-of-type(2)")).getText();
					
					// 버튼 이벤트
					String resvBtnEvent = "";
					if ( "예약가능".equals(stat) ) {
						resvBtnEvent = wEl.findElement(By.cssSelector("td:nth-of-type(3) > button")).getAttribute("onclick");
					}
					
					if (i == 0) {
						result.append("<h1><small>울산 북구 당사오션 오토캠핑장</small></h1>");									
						result.append("<table class=\"table table-striped\">");
						result.append(	"<colgroup><col style=\"width:15%;\" /><col style=\"width:auto;\" /><col style=\"width:25%;\" /><col style=\"width:15%;\" /></colgroup>");
						result.append(	"<thead>");
						result.append(		"<tr>");
						result.append(			"<th>예약일자</th>");
						result.append(			"<th>사이트명</th>");
						result.append(			"<th>예약현황</th>");
						result.append(			"<th>예약실행</th>");
						result.append(		"</tr>");
						result.append(	"</thead>");
						result.append(	"<tbody>");
					}
				
					System.out.println("예약일자 : " + searchVO.getResvDate() + ", 사이트명 : " + siteNm + ", 예약현황 : " + stat);
					
					String resvColor = "label-default";
					if ( "예약가능".equals(stat) ) {
						resvColor = "label-success";
					}
					else if ( "예약종료".equals(stat) ) {
						resvColor = "label-danger";
					}
					
					boolean printYn = false;
					if ( "Y".equals(searchVO.getPrintAllMode()) ) {
						printYn = true;
					}
					else if ( "N".equals(searchVO.getPrintAllMode()) && "예약가능".equals(stat) ) {
						printYn = true;
					}
					
					if (printYn) {
						result.append("	<tr>");
						result.append("		<td>" + searchVO.getResvDate() + "</td>");
						result.append("		<td>" + siteNm + "</td>");
						result.append("		<td><span class=\"label " + resvColor + "\">" + stat + "</span></td>");
						result.append("		<td>");
						if ( !"".equals(resvBtnEvent) ) {
							result.append("		<button type=\"button\" onclick=\"" + resvBtnEvent + "\" class=\"btn btn-xs btn-info\"><span><i class=\"glyphicon glyphicon-ok-circle\"></i> " + searchVO.getResvDate() + " 예약실행</span></button>");
						}
						result.append("		</td>");
						result.append("	</tr>");
					}
					
					if (i == (site_list.size() - 1)) {
						result.append("</tbody>");
						result.append("</table>");
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}

	/**
	 * 당사 오토캠핑장 자동예약 실행
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/dangsaResv.*")
	public void dangsaResv(
			@ModelAttribute("bukguAutoCampVO") BukguAutoCampVO searchVO, 
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
			driver.get("https://camping.ubimc.or.kr/login.do");

			// 통합회원 로그인 버튼 클릭
			WebElement loginBtnEl = driver.findElement(By.id("login_ulsan"));
			loginBtnEl.click();
			
			// 아이디 처리
			WebElement inputIdEl = driver.findElement(By.name("userid"));
			StringSelection stringSelection = new StringSelection(searchVO.getnId());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			Actions builder = new Actions(driver);
			builder.keyDown(inputIdEl, Keys.CONTROL).perform();
			builder.sendKeys(inputIdEl, "v").perform();
			builder.keyUp(inputIdEl, Keys.CONTROL).perform();
			
			// 비밀번호 처리
			WebElement inputPwEl = driver.findElement(By.name("password"));
			stringSelection = new StringSelection(searchVO.getnPw());
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			builder = new Actions(driver);
			builder.keyDown(inputPwEl, Keys.CONTROL).perform();
			builder.sendKeys(inputPwEl, "v").perform();
			builder.keyUp(inputPwEl, Keys.CONTROL).perform();

			// 로그인 버튼 클릭
			loginBtnEl = driver.findElement(By.cssSelector(".form-login > input"));
			loginBtnEl.click();
			
			// 예약 페이지
			driver.get("https://camping.ubimc.or.kr/Pmreservation.do");

			// 당사현대차오션캠프 전환 버튼 클릭
			WebElement campingBtnEl = driver.findElement(By.id("btn_siteKindCode_010200"));
			campingBtnEl.click();
			
			// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
			AutoCampUtil.waitForJavascriptLoad(driver);	
			System.out.println(searchVO.getPageNm() + " 페이지에 연결되었습니다.");

			// 캘린더 날짜 선택
			js.executeScript("$('#resDate').val('" + searchVO.getResvDate() + "');");
			js.executeScript("$('#date_line').html('선택날짜 :" + searchVO.getResvDate() + "');");
			js.executeScript("$('.date_line').show();");
			System.out.println("예약 날짜 선택 : " + searchVO.getResvDate());
			
			// 예약 목록 실행
			js.executeScript("addTable();");
			
			// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
			(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#tableSite")));
			
			/*
			// 예약 신청 클릭 실행
			js.executeScript("subIdx('" + searchVO.getResvIdx() + "');");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}

			// 예약하기 버튼 클릭
			WebElement resvBtnEl = driver.findElement(By.cssSelector(".bttns > button"));
			resvBtnEl.click();
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			
			// 이용안내 팝업 확인
			js.executeScript("fnNextStep2();");
			try {Thread.sleep(INTERRUPTED_TIME);} catch (InterruptedException e) {}
			*/
			
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
