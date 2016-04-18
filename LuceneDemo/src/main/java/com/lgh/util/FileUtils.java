package com.lgh.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * 文件操作工具类
 * 
 * @author liguohua
 *
 */
public class FileUtils {
	/**
	 * 读取文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	public static String readFile(String filePath, String encode) {
		StringBuffer sb = new StringBuffer();
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encode));
				String str = "";
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 操作结果
	 */
	public static Boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			file.delete();
			return true;
		}
		return false;
	}

	public static List<File> getDocxFiles(String dirPath) {
		File[] fileArray = new File(dirPath).listFiles();
		List<File> fileList = new ArrayList<File>();
		for (File file : fileArray) {
			if (isDocxFile(file.getName()))
				fileList.add(file);
		}
		return fileList;
	}

	public static String docToString(File doc) {
		StringBuffer content = new StringBuffer();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(doc);
			XWPFDocument xwpf = new XWPFDocument(fis);
			POIXMLTextExtractor ex = new XWPFWordExtractor(xwpf);
			content.append(ex.getText());
			ex.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content.toString();
	}

	public static boolean isDocxFile(String fileName) {
		if (fileName.lastIndexOf(".docx") > 0) {
			return true;
		} 
		return false;
	}
}
