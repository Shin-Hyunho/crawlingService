package egovframework.crawling.naver.web;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import egovframework.common.util.FileUtil;
import egovframework.crawling.naver.service.NaverBlogCommentVO;


/**
 * 네이버블로그 수집된 댓글 엑셀다운로드
 *
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2022.03.10 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2022.03.10
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class NaverBlogCommentExcelDown extends AbstractExcelView {
	
	public HSSFWorkbook _workbook = null;
	
	@Override
	public void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		_workbook = workbook;
		
		String downFileNm = "";
		int[] columnSize = null;
		String[] columnTitle = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String userAgent = request.getHeader("User-Agent");
		String header = "";
		String fileName = "";
			
		// 다운로드 파일명
		downFileNm = "네이버블로그댓글목록_";
		header = downFileNm + formatter.format(new Date());
		fileName = header + ".xls";
		
		// 엑셀 컬럼 사이즈
		columnSize = new int[]{  
				(200*30), (200*30), (200*30), (500*30)
		};
		
		// 엑셀 헤더
		columnTitle = new String[]{
				"번호", "아이디", "닉네임", "내용"
		};
		
		/*NaverBlogCommentVO commentList = new NaverBlogCommentVO();
		commentList.setBlogId("1111");
		commentList.setNickName("닉네임");
		commentList.setContents("내용");
		List<NaverBlogCommentVO> list = new ArrayList<NaverBlogCommentVO>();
		list.add(commentList);*/
		
		// 댓글 데이터
		List<NaverBlogCommentVO> applicantList = (List<NaverBlogCommentVO>) request.getSession().getAttribute("COMMENT_LIST");
		//System.out.println("***********" + applicantList.size());
		
		// 엑셀 header 생성
		HSSFSheet excelSheet = createFirstSheet(header, columnSize);
		setExcelHeader(excelSheet, header, columnTitle, true);
		setExcelRows(excelSheet, applicantList);
		
		// 다운로드 header
		String mimetype = "application/x-msdownload";
		//response.setBufferSize(fSize);	// OutOfMemeory 발생
		response.setContentType(mimetype);
		//response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fvo.getOrignlFileNm(), "utf-8") + "\"");
		FileUtil.setDisposition(fileName, request, response);
		//response.setContentLength(fSize);
	}
	
	/**
	 * 엑셀 셀 사이즈 설정
	 * @param workbook
	 * @param header
	 * @return
	 */
	private HSSFSheet createFirstSheet(String header, int[] columnSize) {
		HSSFSheet excelSheet = _workbook.createSheet(header);
		if (columnSize.length > 0) {
			for (int i=0;i<columnSize.length;i++) {
				excelSheet.setColumnWidth(i, columnSize[i]);
			}
		}
		
		return excelSheet;
	}
	
	/**
	 * 엑셀 제목 생성
	 * @param excelSheet
	 * @param header
	 */
	public void setExcelHeader(HSSFSheet excelSheet, String header, String[] columnSize, boolean setStyle) {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String commentSearchText = (String)request.getSession().getAttribute("COMMENT_SEARCH_TEXT");
		String commentBlogidOverlap = (String)request.getSession().getAttribute("COMMENT_BLOGID_OVERLAP");
		String addText = " (블로그 아이디 중복표시 : " + commentBlogidOverlap + ", 색인 단어 : " + commentSearchText + ")";
		
		// 첫번째 row는 엑셀파일 제목을 기재
		excelSheet.createRow(0).createCell(0).setCellValue(header + " - 크롤링 프로그램에 의해 수집된 데이터입니다." + addText);
		excelSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		
		if (columnSize.length > 0) {
			
			// 헤더 스타일
			HSSFCellStyle headerStyle = null;
			if (setStyle) {
			    headerStyle = _workbook.createCellStyle();
			    headerStyle.setWrapText(true);
			    
			    // 폰트 설정
			    HSSFFont headerFont = _workbook.createFont();
			    //font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
			    //font.setFontHeightInPoints((short)10);
			    headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			    headerFont.setColor(IndexedColors.WHITE.getIndex());
				headerStyle.setFont(headerFont);
				
				// 배경패턴
				headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				// 배경색
				headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
				//headerStyle.setFillBackgroundColor(new HSSFColor.RED().getIndex());
				
				// border 설정
				/*headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				headerStyle.setBottomBorderColor(HSSFColor.LIGHT_BLUE.index);
				headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
				headerStyle.setBottomBorderColor(HSSFColor.LIGHT_BLUE.index);
				headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				headerStyle.setBottomBorderColor(HSSFColor.LIGHT_BLUE.index);
				headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				headerStyle.setBottomBorderColor(HSSFColor.LIGHT_BLUE.index);*/
			}
			
			// 헤더 ROW 생성
			HSSFRow excelHeaderRow = excelSheet.createRow(1);
			
			for (int i=0;i<columnSize.length;i++) {
				
				// 헤더 컬럼 생성
				HSSFCell excelHeaderCell = excelHeaderRow.createCell(i);
				excelHeaderCell.setCellValue(columnSize[i]);
				
				// 헤더 스타일 적용
				if (setStyle) excelHeaderCell.setCellStyle(headerStyle);
			}
		}
	}

	/**
	 * 엑셀 데이터 생성
	 * @param excelSheet
	 * @param applicantList
	 * @throws Exception 
	 */
	public void setExcelRows(HSSFSheet excelSheet, List<NaverBlogCommentVO> applicantList) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		int record = 1;
		int idx = 1;
		DecimalFormat df = new DecimalFormat("#,###");
		
		try {
			String commentSearchText = ((String)request.getSession().getAttribute("COMMENT_SEARCH_TEXT")).replaceAll(" ", "").toLowerCase();
			String commentBlogidOverlap = ((String)request.getSession().getAttribute("COMMENT_BLOGID_OVERLAP"));
	
			HSSFCellStyle blogIdStyle = _workbook.createCellStyle();
			blogIdStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex()); 
			blogIdStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			
			HSSFCellStyle blogIdNoneStyle = _workbook.createCellStyle();
	
			HSSFCellStyle contentsStyle = _workbook.createCellStyle();
			contentsStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex()); 
			contentsStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			
			// 중복 체크용 블로그 아이디 배열
			List<String> writeBlogIdList = new ArrayList<String>();
			for (Object applicant : applicantList) {
				NaverBlogCommentVO commentVO = (NaverBlogCommentVO) applicant;
				if (commentVO.getBlogId() != null && !"".equals(commentVO.getBlogId())) {
					writeBlogIdList.add(commentVO.getBlogId());
				}
			}
			
			for (Object applicant : applicantList) {
				NaverBlogCommentVO commentVO = (NaverBlogCommentVO) applicant;
				
				HSSFRow excelRow = excelSheet.createRow(++record);
				Cell cell 	= excelRow.createCell(0);
				Cell cell1 	= excelRow.createCell(1);
				Cell cell2 	= excelRow.createCell(2);
				Cell cell3 	= excelRow.createCell(3);
				
				// 블로그 아이디가 중복 확인
				if ("Y".equals(commentBlogidOverlap)) {
					if (commentVO.getBlogId() != null && !"".equals(commentVO.getBlogId())) {
						int isChkBlogIdCnt = Collections.frequency(writeBlogIdList, commentVO.getBlogId());
						if (isChkBlogIdCnt >= 2) {
							cell1.setCellStyle(blogIdStyle);
						}
						else {
							cell1.setCellStyle(blogIdNoneStyle);
						}
					}
					else {
						cell1.setCellStyle(blogIdNoneStyle);
					}
				}
				
				// 댓글 내용 검색 문자열이 포함되었는지 확인
				if (commentSearchText != null && !"".equals(commentSearchText)) {
					if ((commentVO.getContents().replaceAll(" ", "").toLowerCase()).contains(commentSearchText)) {
		                cell3.setCellStyle(contentsStyle);
					}
				}
				
				cell.setCellValue(df.format(idx++));
				cell1.setCellValue(commentVO.getBlogId());
				cell2.setCellValue(commentVO.getNickName());
				cell3.setCellValue(commentVO.getContents());
				
				//System.out.println("**********************" + idx + "/" + commentVO.getNickName());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			request.getSession().setAttribute("RESULT_CONSOLE_FLAG", "error");
			System.out.println("[PROCESS_ERROR] 엑셀파일로 변환 중 오류가 발생되었습니다. 관리자에게 문의하세요.");
			request.getSession().setAttribute("RESULT_CONSOLE_MSG", "[PROCESS_ERROR] 엑셀파일로 변환 중 오류가 발생되었습니다. 관리자에게 문의하세요.");
			throw new RuntimeException(e.getMessage());
		} 
	}
	
}
