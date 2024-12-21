package egovframework.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 관련 함수
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
public class FileUtil {
	/** 다운로드 버퍼 크기 */
	private static final int BUFFER_SIZE = 8192; // 8kb
	/** 문자 인코딩 */
	// private static final String CHARSET = "euc-kr";
	private static final String CHARSET = "UTF-8";
	public void copyDirectory(File sourcelocation, File targetdirectory) throws IOException {
		// 디렉토리인 경우
		if (sourcelocation.isDirectory()) {
			// 복사될 Directory가 없으면 만듭니다.
			if (!targetdirectory.exists()) {
				targetdirectory.mkdir();
			}

			String[] children = sourcelocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourcelocation, children[i]), new File(
						targetdirectory, children[i]));
			}
		} 
		else {
			// 파일인 경우
			InputStream in = new FileInputStream(sourcelocation);
			OutputStream out = new FileOutputStream(targetdirectory);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	public void copyDirectory(String sourceLocation, String targetLocation) throws IOException {
		File sourcelocation = new File(sourceLocation);
		File targetdirectory = new File(targetLocation);
		// 디렉토리인 경우
		if (sourcelocation.isDirectory()) {
			// 복사될 Directory가 없으면 만듭니다.
			if (!targetdirectory.exists()) {
				targetdirectory.mkdir();
			}
			
			String[] children = sourcelocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourcelocation, children[i]), new File(
						targetdirectory, children[i]));
			}
		} 
		else {
			// 파일인 경우
			InputStream in = new FileInputStream(sourcelocation);
			OutputStream out = new FileOutputStream(targetdirectory);
			
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	public boolean deleteFolder(File targetFolder) {

		File[] childFile = targetFolder.listFiles();
		boolean confirm = false;
		int size = childFile.length;

		if (size > 0) {

			for (int i = 0; i < size; i++) {
				if (childFile[i].isFile()) {
					confirm = childFile[i].delete();
				} else {
					deleteFolder(childFile[i]);
				}
			}
		}

		targetFolder.delete();

		return (!targetFolder.exists());
	}// deleteFolder

	public boolean deleteFolder(String targetSrc) {
		File targetFolder = new File(targetSrc);
		File[] childFile = targetFolder.listFiles();
		boolean confirm = false;
		int size = childFile.length;
		
		if (size > 0) {
			
			for (int i = 0; i < size; i++) {
				if (childFile[i].isFile()) {
					confirm = childFile[i].delete();
				} else {
					deleteFolder(childFile[i]);
				}
			}
		}
		
		targetFolder.delete();
		
		return (!targetFolder.exists());
	}// deleteFolder
	
	public List<String> listFile(String targetPath){
		List<String> list = new ArrayList<String>();
		File listFile = new File(targetPath);
		String[] fileArr = listFile.list();
		for(String str:fileArr){
			list.add(str);
		}
		return list;
	}
	
	public String replaceString(String str) {
		String str_imsi = "";
		String[] filter_word = { 
				"\\\\", "\\/", "\\:", "\\?", "\\*", "\\<", "\\>", "\\|", "\\&"
				};

		for (int i = 0; i < filter_word.length; i++) {
			str_imsi = str.replaceAll(filter_word[i], "_");
			str = str_imsi;
		}

		return str;
	}
	
	// 중복 파일 리네임용
	// 중복 파일 정책에 맞는 String return
	public String renamePolicyString (File file){
		
		String returnVal = "";
		String path		 = file.getPath();
		String orgFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
		if(file.isFile()){
			for(int i=1; i< Integer.MAX_VALUE; i++){
				String rename	= orgFileName + "("+i+")";
				File checkFile = new File(path+rename);
				if(!checkFile.isFile()){
					returnVal = rename;
					break;
				}
			}
		}
			
		return returnVal;
	}
	
	public boolean moveFile(String srcPath, String srcFileName, String destPath, String destFileName){
		boolean result = false;
		File srcFile = new File(srcPath+"/"+srcFileName);
		if(srcFile.isFile()){
			File destFolder = new File(destPath);
			if(!destFolder.isDirectory())
				destFolder.mkdirs();
			result = srcFile.renameTo(new File(destPath+"/"+destFileName));
		}
		
		return result;
	}
	
	/**
	 * 파일 삭제
	 * @param fileList
	 * @param path
	 * @return
	 */
	public boolean deleteFile(List<String> fileList, String path){
		boolean result = false;
		for(String fileName : fileList){
			File deleteFile = new File(path+"/"+fileName);
			if(deleteFile.isFile()){
				System.out.println("Delete File : "+ deleteFile.getName());
				result= deleteFile.delete();
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param file 실제 서버의 파일
	 * @param realName 다운로드시 설정해줄 파일명
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void download(HttpServletRequest request,
			HttpServletResponse response, File file, String realName) throws ServletException,
			IOException {

		String mimetype = request.getSession().getServletContext()
				.getMimeType(file.getName());

		if (file == null || !file.exists() || file.length() <= 0
				|| file.isDirectory()) {
			throw new IOException(
					"파일 객체가 Null 혹은 존재하지 않거나 길이가 0, 혹은 파일이 아닌 디렉토리이다.");
		}

		InputStream is = null;

		try {
			is = new FileInputStream(file);
			download(request, response, is, realName, file.length(),
					mimetype);
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
	}
	
	/**
	 * 실제 이름이 없으면 파일 객체에서 이름을 얻어옴.
	 * @param request
	 * @param response
	 * @param file
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void download(HttpServletRequest request,
			HttpServletResponse response, File file) throws ServletException,
			IOException {

		FileUtil.download(request, response, file, file.getName());
	}

	/**
	 * 해당 입력 스트림으로부터 오는 데이터를 다운로드 한다.
	 * 
	 * @param request
	 * @param response
	 * @param is
	 *            입력 스트림
	 * @param filename
	 *            파일 이름
	 * @param filesize
	 *            파일 크기
	 * @param mimetype
	 *            MIME 타입 지정
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void download(HttpServletRequest request,
			HttpServletResponse response, InputStream is, String filename,
			long filesize, String mimetype) throws ServletException,
			IOException {
		String mime = mimetype;

		byte[] buffer = new byte[BUFFER_SIZE];
		
		if (mimetype == null || mimetype.length() == 0) mime = "application/octet-stream;";
		
		try {
			setDisposition(filename, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		// 아래 부분에서 euc-kr 을 utf-8 로 바꾸거나 URLEncoding을 안하거나 등의 테스트를
		// 해서 한글이 정상적으로 다운로드 되는 것으로 지정한다.
		String userAgent = request.getHeader("User-Agent");
		
		// attachment; 가 붙으면 IE의 경우 무조건 다운로드창이 뜬다. 상황에 따라 써야한다.
		if (userAgent != null && userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 이하
			response.setContentType(mime + "; charset=" + CHARSET);
			response.setHeader("Content-Disposition","filename=" + URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20") + ";");
		} else if (userAgent != null && userAgent.indexOf("MSIE 6.0") > -1 || userAgent.indexOf("MSIE 7.0") > -1 
				|| userAgent.indexOf("MSIE 8.0") > -1) { // MSIE (보통은 6.x 이상 가정)
			response.setContentType(mime + "; charset=" + CHARSET);
			response.setHeader("Content-Disposition", "attachment; filename="
					+ java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20") + ";");
		}else if (userAgent != null && userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1 || userAgent.indexOf("Safari") > -1) { // MSIE (보통은9.x 이상 가정),사파리
			if (filename.endsWith("m4v") || filename.endsWith("mp4")) {
				mime = "video/mp4;";
			}
			response.setContentType(mime + "; charset=" + CHARSET);
			response.setHeader("Content-Disposition", "attachment; filename="
					+ java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20") + ";");
		}else { // 모질라나 오페라
			response.setContentType(mime + "; charset=" + CHARSET);
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(filename.getBytes(CHARSET), "latin1") + ";");
		}*/
		
		// 파일 사이즈가 정확하지 않을때는 아예 지정하지 않는다.
		if (filesize > 0) {
			response.setHeader("Content-Length", "" + filesize);
		}

		BufferedInputStream fin = null;
		BufferedOutputStream outs = null;
		try {
			fin = new BufferedInputStream(is);
			outs = new BufferedOutputStream(response.getOutputStream());
			int read = 0;

			while ((read = fin.read(buffer)) != -1) {
				outs.write(buffer, 0, read);
			}
		} catch (IOException ex) {
			// Tomcat ClientAbortException을 잡아서 무시하도록 처리해주는게 좋다.
			ex.printStackTrace();
		} finally {
			try {
				outs.close();
			} catch (Exception ex1) {}

			try {
				fin.close();
			} catch (Exception ex2) {}
		} // end of try/catch
	}
	
	private static String getTimeStamp() {

		String rtnStr = null;

		// 문자열로 변환하기 위한 패턴 설정(년도-월-일 시:분:초:초(자정이후 초))
		String pattern = "yyyyMMddhhmmssSSS";

		SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		rtnStr = sdfCurrent.format(ts.getTime());

		return rtnStr;
	}
	
	/**
	 * 이미지 읽어오기
	 * @param req
	 * @param res
	 * @param file
	 * @throws IOException
	 */
	public static void getImage(HttpServletRequest req,
			HttpServletResponse res, File file) throws IOException{
		
		if (!file.exists()) {
			return;
		}
		int contentLength = (int)file.length();
		
		res.setContentType(new MimetypesFileTypeMap().getContentType(file));
		res.setHeader("Content-Transfer-Encoding", "binary");
		if (contentLength > 0) res.setContentLength(contentLength);
		
		BufferedInputStream fin = null;
		BufferedOutputStream outs = null;
		InputStream is = null;
		
		byte[] buffer = new byte[8192];
		
		try {
			is = new FileInputStream(file);
			fin = new BufferedInputStream(is);
			outs = new BufferedOutputStream(res.getOutputStream());
			int read = 0;
			
			while ((read = fin.read(buffer)) != -1) {
				outs.write(buffer, 0, read);
			}
		} finally {
			try { if (outs != null) outs.close(); } catch (Exception ex1) {}
			try { if (fin != null) fin.close(); } catch (Exception ex1) {}
			try { if (is != null) is.close(); } catch (Exception ex1) {}
		}		
	}
	
	/**
     * 브라우저 구분 얻기.
     *
     * @param request
     * @return
     */
	public static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if (header.indexOf("MSIE") > -1) {
            return "MSIE";
        } else if (header.indexOf("Trident") > -1) {	// IE11 문자열 깨짐 방지
            return "Trident";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        }
        return "Firefox";
    }
    
    /**
     * Disposition 지정하기.
     *
     * @param filename
     * @param request
     * @param response
     * @throws Exception
     */
	public static void setDisposition(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String browser = getBrowser(request);

		String dispositionPrefix = "attachment; filename=";
		String encodedFilename = null;

		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Trident")) { // IE11 문자열 깨짐 방지
			encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
			//encodedFilename = URLDecoder.decode(encodedFilename); // 파이어폭스에서 한글이 깨질때 주석을 해제해보세요
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < filename.length(); i++) {
				char c = filename.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = "\"" + sb.toString() + "\"";
		} else {
			throw new IOException("Not supported browser");
		}

		response.setHeader("Content-Disposition", dispositionPrefix + encodedFilename);

		if ("Opera".equals(browser)) {
			response.setContentType("application/octet-stream;charset=UTF-8");
		}
	}
	
	/**
	 * 파일사이즈 단위 리턴
	 * @param size
	 * @return
	 */
    public static String getFileSize(String size) {
        String gubn[] = {"Byte", "KB", "MB" } ;
        String returnSize = new String ();
        int gubnKey = 0;
        double changeSize = 0;
        long fileSize = 0;
        try{
            fileSize =  Long.parseLong(size);
            for( int x=0 ; (fileSize / (double)1024 ) >0 ; x++, fileSize/= (double) 1024 ){
                gubnKey = x;
                changeSize = fileSize;
            }
            returnSize = changeSize + gubn[gubnKey];
        }catch ( Exception ex){ returnSize = "0.0 Byte"; }
        return returnSize;
    }
}
