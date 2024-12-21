package egovframework.crawling.camp.service;

/**
 * 동구 오토캠핑장 VO
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
public class DongguAutoCampVO extends DefaultVO {
	
	private String gubun;
	private String campPlace;
	private String resvDate;
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
	public String getCampPlace() {
		return campPlace;
	}
	public void setCampPlace(String campPlace) {
		this.campPlace = campPlace;
	}
	public String getResvDate() {
		return resvDate;
	}
	public void setResvDate(String resvDate) {
		this.resvDate = resvDate;
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
