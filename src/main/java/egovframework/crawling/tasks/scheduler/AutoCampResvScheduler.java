package egovframework.crawling.tasks.scheduler;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import org.springframework.stereotype.Component;

import egovframework.crawling.camp.common.AutoCampUtil;
import egovframework.crawling.camp.common.Constant;
import egovframework.crawling.camp.service.AutoCampVO;
import egovframework.crawling.camp.service.JungguAutoCampVO;
import egovframework.crawling.camp.web.JungguCampController;

/**
 * 
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2018.01.11 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2018.01.11
 * @version 1.0
 */
@Component
public class AutoCampResvScheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoCampResvScheduler.class);
	private static final String JUNGGU_URL1 = "https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1010300";
	private static final String JUNGGU_URL2 = "https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1020300";
	private static final String JUNGGU_URL3 = "https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1030300";
	private static final String JUNGGU_URL4 = "https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2030300";
	private static final String JUNGGU_URL5 = "https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2010400";
	
	public void execute() throws Exception {
		/*
		// 현재 날짜 구하기        
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String resvDate = now.format(formatter);
		System.out.println(resvDate);
		
		// 드라이브 연결
		WebDriver driver = AutoCampUtil.createWebDriver(Constant.WEB_DRIVER_ID, Constant.WEB_DRIVER_PATH, "DEBUG");
		
		// 로그인 페이지 연결
		driver.get("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=5010000");
		
		// 아이디 처리
		WebElement inputIdEl = driver.findElement(By.id("chkUserId"));
		StringSelection stringSelection = new StringSelection("hh1225@nate.com");
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		Actions builder = new Actions(driver);
		builder.keyDown(inputIdEl, Keys.CONTROL).perform();
		builder.sendKeys(inputIdEl, "v").perform();
		builder.keyUp(inputIdEl, Keys.CONTROL).perform();
		
		// 비밀번호 처리
		WebElement inputPwEl = driver.findElement(By.id("chkPasswd"));
		stringSelection = new StringSelection("ehfl2685!");
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		builder = new Actions(driver);
		builder.keyDown(inputPwEl, Keys.CONTROL).perform();
		builder.sendKeys(inputPwEl, "v").perform();
		builder.keyUp(inputPwEl, Keys.CONTROL).perform();

		// 로그인 버튼 클릭
		WebElement loginBtnEl = driver.findElement(By.id("login_btn"));
		loginBtnEl.click();
		
		
		// 중구 예약페이지 연결
		driver.get(JUNGGU_URL1);
		
		// 페이지가 정상적으로 로드가 완료 될때까지 대기
		AutoCampUtil.waitForPageLoad(driver, JUNGGU_URL1);
		
		// 자바스크립트가 정상적으로 로그가 완료 될때까지 대기
		AutoCampUtil.waitForJavascriptLoad(driver);
		System.out.println(JUNGGU_URL1 + " 페이지에 연결되었습니다.");
		
		// 스크립트를 사용하기 위한 캐스팅
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		// 달력 readonly 속성 제거 후 예약일자 value 설정
		js.executeScript("$('#calPick').removeAttr('readonly').val('" + resvDate + "');");
		System.out.println("예약 날짜 선택 : " + resvDate);
		
		// 필수 함수 실행
		js.executeScript("fAjaxSiteList('" + resvDate + "');");

		// 예약목록 결과 테이블이 정상적으로 로드가 될때까지 대기
		(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".site_list ul > li")));
		
		// 예약 클릭
		js.executeScript("fAjaxSiteSelect('CP_SITE_000000000105', '" + resvDate + "', 'L0', '오토 201');");
		
		// 안내 Alert 창 닫기
		driver.switchTo().alert().accept();
		
		// 숙박기간 선택 (1 -> 1박, 2 -> 2박)
		js.executeScript("$('#period').val('2');");
		
		// 인원선택
		js.executeScript("$('#people').val('6');");
		
		// 할인정보선택
		js.executeScript("$('#discount').val('OPTION_0000000000141');");
		
		// 차량번호입력
		js.executeScript("$('#carnum').val('10도1500');");
		
		// 동의체크
		js.executeScript("$('#agr1').attr('checked', true);");
		js.executeScript("$('#agr2').attr('checked', true);");
		js.executeScript("$('#agr3').attr('checked', true);");
		js.executeScript("$('#agr4').attr('checked', true);");
		js.executeScript("$('#agr').attr('checked', true);");
		
		// 결제수단 체크
		js.executeScript("$('#pay2').attr('checked', true);");
		
		// 결제버튼 클릭
		WebElement payBtnEl = driver.findElement(By.className("pay_btn"));
		payBtnEl.click();
		*/
    }
}
