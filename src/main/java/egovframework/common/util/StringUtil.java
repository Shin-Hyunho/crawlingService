package egovframework.common.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 문자열 관련 함수 
 *
 * <pre>
 * &lt;&lt;개정이력(Modification Information)&gt;&gt;
 * 2015.04.10 신현호
 * 최초 생성
 * </pre>
 *
 * @author 개발팀 신현호
 * @since 2015.04.10
 * @version 1.0
 */
public enum StringUtil {

	INSTANCE, ;

	public static void main(String[] args) {
		/* ===== test for isBlank(arg) ===== */
		System.out.println(isBlank(null)); // result: TRUE
		System.out.println(isBlank("")); // result: TRUE
		System.out.println(isBlank(" ")); // result: TRUE
		System.out.println(isBlank("	")); // result: TRUE
		System.out.println(isBlank("    ")); // result: TRUE
		System.out.println(isBlank(" a ")); // result: FALSE
		System.out.println(isBlank("aaa")); // result: FALSE

		/* ===== test for turnToNullWhenItsBlankValue(arg) */
		System.out.println(changeBlankToNull(null)); // result: null
		System.out.println(changeBlankToNull("")); // result: null
		System.out.println(changeBlankToNull(" ")); // result: null
		System.out.println(changeBlankToNull("	")); // result: nulll
		System.out.println(changeBlankToNull("    ")); // result: nulll
		System.out.println(changeBlankToNull(" a ")); // result: [ a ]
		System.out.println(changeBlankToNull("aaa")); // result: [aaa]

	}

	public static boolean isLegalExtension(String fileName) {
		return isNotBlank(fileName) &&
			!fileName.endsWith(".jsp") &&
			!fileName.endsWith(".sh") &&
			!fileName.endsWith(".asp");
	}

	public static boolean contains(String key, final String[] arr) {
		for (String str : arr) {
			if (str.equals(key)) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public static boolean isNumber(String arg) {
		return isNotBlank(arg) && arg.matches("^[0-9]+$");
	}

	public static boolean isNotNumber(String arg) {
		return !isNumber(arg);
	}

	/**
	 * 파라미터가 null, 탭 문자, 공백 문자, 공백 문자열인 경우 TRUE
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isBlank(String arg) {
		if (arg == null) {
			return Boolean.TRUE;
		} else if (arg.trim().equals("")) {
			return Boolean.TRUE;
		} else if (arg.trim().equals("null")) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static boolean isNotBlank(String arg) {
		return !isBlank(arg);
	}

	public static boolean hasBlankValue(String... args) {
		if (args != null) {
			for (String arg : args) {
				if (isBlank(arg)) {
					return Boolean.TRUE;
				}
			}
		}

		return Boolean.FALSE;
	}

	public static boolean noBlankValue(String... args) {
		return !hasBlankValue(args);
	}

	public static boolean areAllBlankValue(String... args) {
		if (args != null) {
			for (String arg : args) {
				if (isNotBlank(arg)) {
					return Boolean.FALSE;
				}
			}
		}

		return Boolean.TRUE;
	}

	public static String changeBlankToNull(String arg) {
		return isBlank(arg) ? null : arg;
	}

	public static String changeBlankToBlank(String arg) {
		return isBlank(arg) ? "" : arg;
	}

	public static String concat(String token, String... args) {
		StringBuilder rtn = new StringBuilder();
		if (args != null) {
			for (String arg : args) {
				if (StringUtil.isNotBlank(rtn.toString())) {
					rtn.append(token);
				}
				rtn.append(arg);
			}
		}

		return rtn.toString();
	}

	public static String trim(String arg) {
		return isNotBlank(arg) ? arg.trim() : "";
	}
	
    public static String xssUnscript(String data) {
        if (data == null || data.trim().equals("")) {
            return "";
        }
        
        String ret = data;
        
        ret = ret.replaceAll("<(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;script");
        ret = ret.replaceAll("</(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;/script");
        
        ret = ret.replaceAll("<(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;object");
        ret = ret.replaceAll("</(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;/object");
        
        ret = ret.replaceAll("<(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;applet");
        ret = ret.replaceAll("</(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;/applet");
        
        ret = ret.replaceAll("<(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");
        ret = ret.replaceAll("</(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");
        
        ret = ret.replaceAll("<(F|f)(O|o)(R|r)(M|m)", "&lt;form");
        ret = ret.replaceAll("</(F|f)(O|o)(R|r)(M|m)", "&lt;form");

        return ret;
    }

	public static String[] getArrValueParse(String val) {
		return val.split(",");
	}
	
	public static String[] getArrValueParse(String val, int limit) {
		return val.split(",", limit);
	}
	
	/**
	 * 사용자의 IP를 return
	 *
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("x-forwarded-for");
		if (remoteAddr == null || remoteAddr.length() == 0 || remoteAddr.toLowerCase().equals("unknown")) {
			remoteAddr = request.getHeader("REMOTE_ADDR");
		}

		if (remoteAddr == null || remoteAddr.length() == 0 || remoteAddr.toLowerCase().equals("unknown")) {
			remoteAddr = request.getRemoteAddr();
		}

		return remoteAddr;
	}
	
	public static Map<String, String> getStrFormParamMap(String val) {
		String[] parseArr = val.split("&");
		Map<String, String> returnMap = new HashMap<String, String>();
		
		for (String value : parseArr) {
			returnMap.put(value.split("=")[0], value.split("=")[1]);
		}
		
		return returnMap;
	}
	
	/**
	 * 문자열에 포함되어 있는 모든 공백 제거
	 * IDEOGRAPHIC SPACE 라 불리는 유니코드 \u3000, HTML 표현으로는 &#12288; 폰트 지원이 없으면 눈에 보이지 않는(display 되지 않는) 코드로만 존재하는 공백 등 제거
	 * @param str
	 * @return
	 */
	public static String strUnBlankAll(String str) {
		str = str.replaceAll(" ", ""); 
		str = str.replaceAll("\\p{Z}", "");
		str = str.replaceAll("\\p{Space}", "");
		
		return str;
	}
	
	/**
	 * HTML 태그를 제거
	 * @param str
	 * @return
	 */
	public static String htmlClear(String str) {
		return str.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	/**
	 * 모든 태그를 삭제하여 치환한다.
	 * @param str
	 * @return
	 */
	public static String removeAllTag(String str) {
		return str.replaceAll("<[^>]*>", " ");
    }
	
	/**
	 * 들여쓰기 탭 삭제 치환
	 * @param str
	 * @return
	 */
	public static String removeTabTag(String str) {
		return str.replaceAll("\t", " ");
    }
	
	/**
	 * 입력된 문자열에서 숫자외 문자열은 삭제
	 * @param str
	 * @return
	 */
	public static String removeNotNumber(String str) {
		return str.replaceAll("[^0-9]", "");
    }
	
	/**
	 * 입력된 문자열이 null 인지 체크
	 * @param obj
	 * @return
	 */
	public static String nullToString(Object obj) {
		if (obj == null)
			return ""; 
		return obj.toString();
	}
	 
	/**
	 * 배열객체에서 특정문자열이 있는지 확인
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean findArray(String[] arr, String s) {
	    boolean returnValue = false;
	    for (String str : arr) {
	      if (str.equals(nullToString(s)))
	        returnValue = true; 
	    } 
	    return returnValue;
	}
}
