package egovframework.common.util;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * JavaScript Alert 출력, Redirects
 *
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2016.04.11 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2016.04.11
 * @version 1.0
 */
public enum WriterUtil {

	INSTANCE, ;

	public static final Logger LOGGER = Logger.getLogger(WriterUtil.class.getName());

	public static void flushJSAlert(HttpServletResponse response, String contents) {
		Writer writer = null;
		try {
			response.setContentType("text/html; charset=UTF-8");
			writer = response.getWriter();
			writer.write(contents);
		} catch (IOException e) {
			LOGGER.error("IOException - can not write response content");
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				LOGGER.error("IOException - can not close writer object");
			}
		}
	}

	public static String createJsAlertContent(String msg, String returnPath) {
		StringBuilder content = new StringBuilder();
		content.append("<script>");
		if (StringUtil.isNotBlank(msg)) {
			content.append("alert('" + msg + "');");
		}
		content.append(returnPath);
		content.append("</script>");
		return content.toString();
	}

	public static void flushJSAlertNotExistBoard(HttpServletResponse response) {
		flushJSAlertNotExistBoard(response, "location.href='/'");
	}

	public static void flushJSAlertNotExistBoard(HttpServletResponse response, String returnPath) {
		flushJSAlert(response, createJsAlertContent("게시판이 삭제되었거나 존재하지 않습니다.", returnPath));
	}

	public static void flushJSSysInvalidAccess(HttpServletResponse response) {
		flushJSInvalidAccess(response, "location.href='/sys/'");
	}

	public static void flushJSInvalidAccess(HttpServletResponse response) {
		flushJSInvalidAccess(response, "location.href='/'");
	}

	public static void flushJSInvalidAccess(HttpServletResponse response, String returnPath) {
		flushJSAlert(response, createJsAlertContent("잘못된 접근입니다.", returnPath));
	}

	public static void flushJsAlertAndHistoryBack(HttpServletResponse response, String msg) {
		flushJSAlert(response, createJsAlertContent(msg, "history.back(-1);"));
	}

	public static void flushJsAlertAndPopupClose(HttpServletResponse response, String msg) {
		flushJSAlert(response, createJsAlertContent(msg, "window.close();"));
	}

	public static void flushJsAlertAndPopCloseParentReload(HttpServletResponse response, String msg) {
		flushJSAlert(response, createJsAlertContent(msg, "opener.parent.location.reload();window.close();"));
	}

}

