package com.lgh.dataStruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieMap {
	private WordNode root;
	public int minMatchType = 1;      
	public int maxMatchType = 2;
	private String ENCODING = "GBK";
	private int wordCount=0;
	private String filePath="E:/java备份/java/敏感词过滤/敏感词库/sensitiveWord.txt";
	public Map<String,Double>wordRateMap=new HashMap<String,Double>();
	
	public static class WordNode{
		public String isEnd="0";
		public Map<Object,WordNode>nodeMap=new HashMap<Object,WordNode>();
	}
	public TrieMap(String ENCODING, String filePath) {
		this.ENCODING = ENCODING;
		this.filePath = filePath;
	}
	/**
	 * 初始化敏感词库配置
	 * @return
	 */
	public void initKeyWordConfig(){
		try {
			List<String> keyWordList = readWordFile(filePath,ENCODING);
			addWordToHashMap(keyWordList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加单词 
	 * @param keyWordList
	 */
    public synchronized void addWordToHashMap(List<String> keyWordList) { 
    	if(root==null)
    		root=new WordNode();
    	for(String key:keyWordList){
        	WordNode currentWordNode=root;
        	for(int i=0;i<key.length();i++){
        		char keyChar=key.charAt(i);
        		WordNode wordMap=currentWordNode.nodeMap.get(keyChar);
        		if(wordMap!=null)
        			currentWordNode=wordMap;
        		else{
        	        WordNode newWorNode =new WordNode(); 
        	        newWorNode.isEnd="0";
        	        currentWordNode.nodeMap.put(keyChar, newWorNode);
        	        currentWordNode=newWorNode;
        		}
        		if(i==key.length()-1){
        			if("0".equals(currentWordNode.isEnd)){
        				currentWordNode.isEnd="1";
        				wordCount++;
        				wordRateMap.put(key, 1.0);
        			}
        			else{
        				wordRateMap.put(key, wordRateMap.get(key)+1);
        			}
        		}
        	}
        }
    }  
    
    /**
     * 读取敏感词库
     * @param filePath
     * @param encode
     * @return
     * @throws Exception
     */
	@SuppressWarnings("resource")
	private List<String> readWordFile(String filePath,String encode) throws Exception{
		List<String> list = null;
		File file = new File(filePath);    
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),encode);
		try {
			if(file.isFile() && file.exists()){      
				list = new ArrayList<String>();
				BufferedReader bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    
					list.add(txt);
			    }
			}
			else{         
				throw new Exception("加载敏感词库出错了！");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			read.close();     
		}
		return list;
	}
	public WordNode getRoot() {
		return root;
	}
	/**
	 * 统计敏感词库中敏感词的数量
	 * @return
	 */
	public int getSensitiveWordCount(){
		return wordCount;
	}
}
