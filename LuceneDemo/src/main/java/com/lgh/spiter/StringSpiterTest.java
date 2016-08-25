package com.lgh.spiter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.lgh.lucene.LuceneDemo;

public class StringSpiterTest {
	@Test
	public void test1() throws IOException{
		Excel<SensitiveWord> excel = new Excel<SensitiveWord>();
		String path="E:/java备份/测试/敏感词过滤";
		String fileName="game导出模板.xlsx";
		ArrayList<ArrayList<String>>wordRowList=null;
		ArrayList<SensitiveWord>sensitiveWordList=new ArrayList<SensitiveWord>();
		try {
			wordRowList=excel.readExcel(fileName, path);
			for(int index=0;index<wordRowList.size();index++){
				List<String>wordRow=wordRowList.get(index);
				for(String word:wordRow){
					if(word!=null&&!"".equals(word)){
						SensitiveWord sensitiveWord=new SensitiveWord();
						sensitiveWord.setWord(word);
						sensitiveWordList.add(sensitiveWord);
					}
				}
			}
			System.out.println("敏感词一共有:"+sensitiveWordList.size()+"个");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String storePath="E:/java备份/测试/敏感词过滤/模板.xlsx";
		Workbook resultBook = null;
		try {
			resultBook=excel.CreateExcel(sensitiveWordList, storePath, 2, 0, null, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String tempPath = "E:/java备份/测试/敏感词过滤/导出数据.xlsx";
        File file = new File(tempPath);
        FileOutputStream fos = new FileOutputStream(file);
        resultBook.write(fos);
        fos.close();
        
		/*List<String>docList=new ArrayList<String>();
		LuceneDemo demo = new LuceneDemo("E:/java备份/测试/lucene");
		Map<String, String> resultMap = demo.searchIndex("现代生活化学");
		Set<String> keySet = resultMap.keySet();
		for (String key : keySet) {
			String txt = resultMap.get(key);
			boolean hasword=false;
			for(int index=0;index<wordRowList.size();index++){
				List<String>wordRow=wordRowList.get(index);
				for(String word:wordRow){
					//System.out.print(word);
					if(txt.contains(word)){
						hasword=true;
						//System.out.println(resultMap.get(key));
						docList.add(key);
						break;
					}
				}
				if(hasword){
					break;
				}
			}
		}
		for(String doc:docList){
			System.out.println(doc);
		}*/
		/*for(String word:sensitiveWordList){
			System.out.println(word);
		}*/
		
		/*List<String>docList=new ArrayList<String>();
		LuceneDemo demo = new LuceneDemo("E:/java备份/测试/lucene");
		//demo.createIndex("E:/学习资料/大学/选修/");
		Map<String, String> resultMap = demo.searchIndex("音乐");
		Set<String> keySet = resultMap.keySet();
		System.out.println(keySet.size());
		for (String key : keySet) {
			String txt = resultMap.get(key);
			for(String word:sensitiveWordList){
				//System.out.println(word);
				if(word.contains("+")){
					//System.out.println(word);
					String[]wordArr=word.split("\\+");
					if(txt.contains(wordArr[0].trim())&&txt.contains(wordArr[1].trim())){
						System.out.println(key+"命中"+word);
						docList.add(key);
						break;
					}
				}
				else{					if(txt.contains(word)){
						System.out.println(key+"命中"+word);
						docList.add(key);
						break;
					}
				}
			}
		}
		for(String doc:docList){
			System.out.println(doc);
		}*/
	}
	@Test
	public void test2(){
		String str="色情 + AV";
		//System.out.println(str.contains("+"));
		String[]wordArr=str.split("\\+");
		
		for(String wd:wordArr){
			System.out.println(wd.trim().matches("^[\u2E80-\u9FFF]+$"));
		}
	}
}
