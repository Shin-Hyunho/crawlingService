package egovframework.crawling.naver.service;

/**
 * 네이버 블로그 댓글 VO
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
public class NaverBlogCommentVO {
	
	private String blogId;
	private String nickName;
	private String contents;
	
	public String getBlogId() {
		return blogId;
	}
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
}
