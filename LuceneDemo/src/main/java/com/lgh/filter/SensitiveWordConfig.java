package com.lgh.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SensitiveWordConfig {
	private WordNode root;
	public int minMatchType = 1;      
	public int maxMatchType = 2;
	private String ENCODING = "GBK";
	private int wordCount=0;
	private String filePath="E:/java备份/java/敏感词过滤/敏感词库/sensitiveWord.txt";
	
	public static class WordNode{
		public String isEnd="0";
		public Map<Object,WordNode>nodeMap=new HashMap<Object,WordNode>();
	}
	public SensitiveWordConfig(String ENCODING, String filePath) {
		this.ENCODING = ENCODING;
		this.filePath = filePath;
		initKeyWordConfig();
	}
	/**
	 * 初始化敏感词库配置
	 * @return
	 */
	public void initKeyWordConfig(){
		try {
			Set<String> keyWordSet = readSensitiveWordFile(filePath,ENCODING);
			addSensitiveWordToHashMap(keyWordSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
     * 将敏感词放入HashSet中，构建一个DFA算法模型 
     * @param keyWordSet  敏感词库 
     */  
    public synchronized void addSensitiveWordToHashMap(Set<String> keyWordSet) { 
    	if(root==null)
    		root=new WordNode();
    	for(String key:keyWordSet){
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
        		if(i==key.length()-1&&"0".equals(currentWordNode.isEnd)){
        			currentWordNode.isEnd="1";
        			wordCount++;
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
	private Set<String> readSensitiveWordFile(String filePath,String encode) throws Exception{
		Set<String> set = null;
		File file = new File(filePath);    
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),encode);
		try {
			if(file.isFile() && file.exists()){      
				set = new HashSet<String>();
				BufferedReader bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    
					set.add(txt);
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
		return set;
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
