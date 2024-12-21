package egovframework.crawling.camp.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.crawling.camp.common.AutoCampUtil;
import egovframework.crawling.camp.common.Constant;
import egovframework.crawling.camp.service.AutoCampVO;
import egovframework.crawling.camp.service.BukguAutoCampVO;
import egovframework.crawling.camp.service.DongguAutoCampVO;
import egovframework.crawling.camp.service.JungguAutoCampVO;
import egovframework.crawling.camp.service.UljuAutoCampVO;
import egovframework.crawling.camp.service.UljuReportAutoCampVO;

/**
 * 오토캠핑장 크롤링
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
public class AutoCampController {
	private static final String TAG 	= "NaverBlogController";
	private static final Logger LOGGER 	= LoggerFactory.getLogger(AutoCampController.class);
		
	// interrupted time
	private static final int INTERRUPTED_TIME 	= 500;
	
	// 드라이버 ID
	private static final String WEB_DRIVER_ID 	= "webdriver.chrome.driver"; 
	
	// 드라이버 경로
	private static String WEB_DRIVER_PATH = "D:\\Dev\\workspace-egov3.7\\crawlingService\\src\\main\\webapp\\WEB-INF\\lib\\chromedriver.exe";

	/**
	 * 크롤링 정보입력
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/crawling.*")
	public String resvCrawling( 
			HttpServletRequest request, 
			HttpServletResponse response,
			ModelMap model) throws Exception {

		// 드라이버 설정
		try {
			System.out.println("[PROCESS INFO] 시스템 환경을 설정하고 있습니다.");
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
			model.addAttribute("systemProp", true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[PROCESS ERROR] 시스템 환경 설정 중 오류가 발생되었습니다.");
			model.addAttribute("systemProp", false);
		}
		
		String path = "egovframework/story/crawling/autoCamp/resv/crawling";
		LOGGER.info("path : {}", path);
		
		return path;
	}

	/**
	 * 전체 일괄 오토캠핑장 크롤링
	 * @param req
	 * @param res
	 * @param model
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping("/resvAllCrawling.*")
	public void resvAllCrawling(
			@ModelAttribute("autoCampVO") AutoCampVO searchVO, 
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
			
			// 중구
			JungguCampController jungguCtr = new JungguCampController();
			JungguAutoCampVO jungguAutoCampVO = new JungguAutoCampVO();
			jungguAutoCampVO.setGubun(searchVO.getGubun());
			jungguAutoCampVO.setResvDate(searchVO.getResvDate());
			jungguAutoCampVO.setConNights(searchVO.getConNights());
			jungguAutoCampVO.setPrintAllMode(searchVO.getPrintAllMode());
			jungguAutoCampVO.setnId(searchVO.getnId());
			jungguAutoCampVO.setnPw(searchVO.getnPw());
			jungguAutoCampVO.setDebug(searchVO.getDebug());
			
			jungguAutoCampVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1010300");
			jungguAutoCampVO.setPageNm("입화산 제2오토캠핑장");
			result.append(jungguCtr.jungguCrawlingProc(driver, jungguAutoCampVO, campPlaceList));
			jungguAutoCampVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1020300");
			jungguAutoCampVO.setPageNm("황방산 오토캠핑장");
			result.append(jungguCtr.jungguCrawlingProc(driver, jungguAutoCampVO, campPlaceList));
			jungguAutoCampVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=1030300");
			jungguAutoCampVO.setPageNm("태화연 오토캠핑장");
			result.append(jungguCtr.jungguCrawlingProc(driver, jungguAutoCampVO, campPlaceList));
			jungguAutoCampVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2030300");
			jungguAutoCampVO.setPageNm("입화산 자연휴양림(야영장)");
			result.append(jungguCtr.jungguCrawlingProc(driver, jungguAutoCampVO, campPlaceList));
			jungguAutoCampVO.setPageUrl("https://camping.junggu.ulsan.kr/camping/pageCont.do?menuNo=2010400");
			jungguAutoCampVO.setPageNm("입화산 자연휴양림(별뜨락 카라반)");
			result.append(jungguCtr.jungguCrawlingProc(driver, jungguAutoCampVO, campPlaceList));
			
			// 동구
			DongguCampController dongguCtr = new DongguCampController();
			DongguAutoCampVO dongguAutoCampVO = new DongguAutoCampVO();
			dongguAutoCampVO.setGubun(searchVO.getGubun());
			dongguAutoCampVO.setResvDate(searchVO.getResvDate());
			dongguAutoCampVO.setConNights(searchVO.getConNights());
			dongguAutoCampVO.setPrintAllMode(searchVO.getPrintAllMode());
			dongguAutoCampVO.setnId(searchVO.getnId());
			dongguAutoCampVO.setnPw(searchVO.getnPw());
			dongguAutoCampVO.setDebug(searchVO.getDebug());
			
			dongguAutoCampVO.setPageUrl("https://daewangam.donggu.ulsan.kr/camping/Pmreservation.do");
			dongguAutoCampVO.setPageNm("대왕암 오토캠핑장");
			result.append(dongguCtr.dongguCrawlingProc(driver, dongguAutoCampVO, campPlaceList));
			
			// 북구
			BukguCampController bukguCtr = new BukguCampController();
			BukguAutoCampVO bukguAutoCampVO = new BukguAutoCampVO();
			bukguAutoCampVO.setGubun(searchVO.getGubun());
			bukguAutoCampVO.setResvDate(searchVO.getResvDate());
			bukguAutoCampVO.setConNights(searchVO.getConNights());
			bukguAutoCampVO.setPrintAllMode(searchVO.getPrintAllMode());
			bukguAutoCampVO.setnId(searchVO.getnId());
			bukguAutoCampVO.setnPw(searchVO.getnPw());
			bukguAutoCampVO.setDebug(searchVO.getDebug());
			
			bukguAutoCampVO.setPageUrl("https://camping.ubimc.or.kr/Pmreservation.do");
			bukguAutoCampVO.setPageNm("울산 북구 강동오토캠핑장");
			result.append(bukguCtr.bukguCrawlingProc(driver, bukguAutoCampVO, campPlaceList));
			
			// 울주군
			UljuCampController uljuCtr = new UljuCampController();
			UljuAutoCampVO uljuAutoCampVO = new UljuAutoCampVO();
			uljuAutoCampVO.setGubun(searchVO.getGubun());
			uljuAutoCampVO.setResvDate(searchVO.getResvDate());
			uljuAutoCampVO.setConNights(searchVO.getConNights());
			uljuAutoCampVO.setPrintAllMode(searchVO.getPrintAllMode());
			uljuAutoCampVO.setnId(searchVO.getnId());
			uljuAutoCampVO.setnPw(searchVO.getnPw());
			uljuAutoCampVO.setDebug(searchVO.getDebug());
			
			uljuAutoCampVO.setPageUrl("https://camping.ulju.ulsan.kr/Pmreservation.do");
			result.append(uljuCtr.uljuCrawlingProc(driver, uljuAutoCampVO, campPlaceList));
			
			// 울주해양
			UljuReportCampController uljuReportCtr = new UljuReportCampController();
			UljuReportAutoCampVO uljuReportVO = new UljuReportAutoCampVO();
			uljuReportVO.setGubun(searchVO.getGubun());
			uljuReportVO.setDay(searchVO.getDay());
			uljuReportVO.setConNights(searchVO.getConNights());
			uljuReportVO.setPrintAllMode(searchVO.getPrintAllMode());
			uljuReportVO.setnId(searchVO.getnId());
			uljuReportVO.setnPw(searchVO.getnPw());
			uljuReportVO.setDebug(searchVO.getDebug());
			
			uljuReportVO.setCateNo("a");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			uljuReportVO.setCateNo("b");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			uljuReportVO.setCateNo("c");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			uljuReportVO.setCateNo("d");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			uljuReportVO.setCateNo("e");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			uljuReportVO.setCateNo("f");
			uljuReportVO.setPageUrl("https://www.xn--om2bi2o9qdy7a48exzk3vf68fzzd.kr/reserve/month?day=" + searchVO.getDay() + "&cate_no=" + uljuReportVO.getCateNo());
			uljuReportVO.setPageNm("울주해양레포츠센터 " + uljuReportVO.getCateNo().toUpperCase() + "구역");
			result.append(uljuReportCtr.uljuReportCrawlingProc(driver, uljuReportVO, campPlaceList));
			
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
}
