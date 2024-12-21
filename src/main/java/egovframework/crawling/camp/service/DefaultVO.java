package egovframework.crawling.camp.service;

/**
 * 크롤링 기본 VO
 * 
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 * 
 * 2022.03.10 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2022.03.10
 * @version 1.0
 *
 */
public class DefaultVO {
	
	private String pageUrl;
	private String pageNm;
	private String resvIdx;
	private int doneElCnt;
	
	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPageNm() {
		return pageNm;
	}

	public void setPageNm(String pageNm) {
		this.pageNm = pageNm;
	}

	public String getResvIdx() {
		return resvIdx;
	}

	public void setResvIdx(String resvIdx) {
		this.resvIdx = resvIdx;
	}

	public int getDoneElCnt() {
		return doneElCnt;
	}

	public void setDoneElCnt(int doneElCnt) {
		this.doneElCnt = doneElCnt;
	}
}
