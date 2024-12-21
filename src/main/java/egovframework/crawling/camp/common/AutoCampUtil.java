package egovframework.crawling.camp.common;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public enum AutoCampUtil {

	INSTANCE, ;

	public static void main(String[] args) {
	
	}
	
	public static WebDriver createWebDriver(String webDriverId, String webDriverPath, String debug) {
		// 드라이버 설정
		try {
			System.out.println("크롤링에 필요한 시스템 환경 설정이 완료 되었습니다.");
			System.setProperty(webDriverId, webDriverPath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] 크롤링에 필요한 시스템 환경 설정 중 오류가 발생되었습니다.");
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
			/*DesiredCapabilities capability = DesiredCapabilities.chrome();
			driver = new RemoteWebDriver(new URL("http://106.247.251.2:28081"), capability);*/
			
			System.out.println("웹 드라이버 설정하고 연결합니다.");
			
			return driver;
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] 웹 드라이버를 연결 및 설정에 오류가 발생하였습니다. 관리자에게 문의하십시오.");
			throw new RuntimeException(e.getMessage());
		} 
	}
	
	public static boolean waitForPageLoad(WebDriver driver, String url) {
		boolean result = new WebDriverWait(driver, 30).until(ExpectedConditions.urlToBe(url));
		System.out.println("waitForPageLoad : " + result);

		return result;
	}
	
	public static boolean waitForJavascriptLoad(WebDriver driver) {
		boolean result = new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd -> 
			((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
		System.out.println("waitForJavascriptLoad : " + result);
		
		return result;
	}
	
	public static void presenceOfElementLocated(WebDriver driver, By by) {
		(new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(by));
		System.out.println("presenceOfElementLocated : " + (new WebDriverWait(driver, 30)).until(ExpectedConditions.presenceOfElementLocated(by)));
	}
	
	public static boolean isWaitElementPooling(WebDriver driver, String cssSelector, int waitTime) {
		boolean isChk = false;
		try {
			for (int i=0;i<waitTime;i++) {
				WebElement wEl = driver.findElement(By.cssSelector(cssSelector));
				if (wEl != null) {
					isChk = true;
					System.out.println("다음 스택을 처리 진행합니다.");
				}
				else {
					isChk = false;
					System.out.println("선 실행된 스택을 처리하고 있습니다!.");
				}
			}
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
		} catch (Exception e) {
			isChk = false;
			System.out.println("선 실행된 스택을 처리하고 있습니다!.");
		}	
		return isChk;
	}
	
	/**
	 * 
	 * @param driver
	 * @param cssSelector
	 * @param doneElCnt : 완료할 객체의 갯수
	 * @param waitTime : 대기 시간
	 * @return
	 */
	public static boolean isWaitElementCntPooling(WebDriver driver, String cssSelector, int doneElCnt, int waitTime) {
		boolean isChk = false;
		try {
			for (int i=0;i<waitTime;i++) {
				List<WebElement>  wElList = driver.findElements(By.cssSelector(cssSelector));
				System.out.println("선 실행된 동적객체의 생성이 완료될때까지 기다리고 있습니다!. (" + wElList.size() + " / " + doneElCnt + ")");
				
				if (wElList.size() <= doneElCnt) {
					System.out.println("선 실행된 동적객체의 생성이 완료되어 다음 스택을 진행합니다.");
					break;
				}
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			
		} catch (Exception e) {
			isChk = false;
			System.out.println("선 실행된 동적객체 확인 중 오류가 발생 되었습니다.");
		}	
		return isChk;
	}
}
