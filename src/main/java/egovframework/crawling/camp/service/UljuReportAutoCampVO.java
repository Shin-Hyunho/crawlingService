package egovframework.crawling.camp.service;

/**
 * 울주군 오토캠핑장 VO
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
public class UljuReportAutoCampVO extends DefaultVO {
	
	private String gubun;
	private String day;
	private String cateNo;
	private String conNights;
	private String printAllMode;
	private String nId;
	private String nPw;
	private String debug = "DEBUG";
	
	public String getGubun() {
		return gubun;
	}
	public void setGubun(String gubun) {
		this.gubun = gubun;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getCateNo() {
		return cateNo;
	}
	public void setCateNo(String cateNo) {
		this.cateNo = cateNo;
	}
	public String getConNights() {
		return conNights;
	}
	public void setConNights(String conNights) {
		this.conNights = conNights;
	}
	public String getPrintAllMode() {
		return printAllMode;
	}
	public void setPrintAllMode(String printAllMode) {
		this.printAllMode = printAllMode;
	}
	public String getnId() {
		return nId;
	}
	public void setnId(String nId) {
		this.nId = nId;
	}
	public String getnPw() {
		return nPw;
	}
	public void setnPw(String nPw) {
		this.nPw = nPw;
	}
	public String getDebug() {
		return debug;
	}
	public void setDebug(String debug) {
		this.debug = debug;
	}
}
