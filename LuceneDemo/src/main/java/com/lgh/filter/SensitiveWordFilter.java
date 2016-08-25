package com.lgh.filter;

import java.util.HashSet;
import java.util.Set;

import com.lgh.filter.SensitiveWordConfig.WordNode;

public class SensitiveWordFilter {
	private SensitiveWordConfig config;
	
	public SensitiveWordFilter(SensitiveWordConfig config) {
		this.config=config;
	}
	
	/** 
     * 检查文字中是否包含敏感字符，如果存在，则返回敏感词字符的长度，不存在返回0 
     * @param txt 
     * @param beginIndex 
     * @param matchType 
     * @return
     */  
	public int CheckSensitiveWord(String txt,int beginIndex,int matchType){  
        boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况  
        int matchFlag = 0;     //匹配标识数默认为0  
        char word = 0; 
        WordNode currentWordNode = config.getRoot();  
        
        for(int i=beginIndex;i<txt.length();i++){
        	word=txt.charAt(i);
        	currentWordNode=currentWordNode.nodeMap.get(word);
        	if(currentWordNode!=null){
        		matchFlag++;
        		if("1".equals(currentWordNode.isEnd)){
        			flag=true;
        			if(config.minMatchType==matchType){
        				break;
        			}
        		}
        	}
        	else 
        		break;
        }
        if(matchFlag < 2 && !flag){       
            matchFlag = 0;  
        }  
        return matchFlag;  
    } 
	
	/**
	 * 判断文本中是否出现敏感词
	 * @param txt
	 * @param matchType
	 * @return
	 */
	public boolean isContaintSensitiveWord(String txt,int matchType){
		boolean flag = false;
		for(int i = 0 ; i < txt.length() ; i++){
			int matchFlag =CheckSensitiveWord(txt, i, matchType); 
			if(matchFlag > 0){    
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 获取文本中出现的敏感词
	 * @param txt
	 * @param matchType
	 * @return
	 */
	public Set<String> getSensitiveWord(String txt , int matchType){
		Set<String> sensitiveWordList = new HashSet<String>();
		for(int i = 0 ; i < txt.length() ; i++){
			int length = CheckSensitiveWord(txt, i, matchType);    
			if(length > 0){    
				sensitiveWordList.add(txt.substring(i, i+length));
				i = i + length - 1;    
			}
		}
		return sensitiveWordList;
	}
	
	/**
	 * 替换文本中出现的敏感词
	 * @param txt
	 * @param matchType
	 * @param replaceChar
	 * @return
	 */
	public String replaceSensitiveWord(String txt,int matchType,String replaceChar){
		String resultTxt = txt;
		Set<String> set = getSensitiveWord(txt, matchType);     
		String replaceString = null;
		for(String word:set){
			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);
		}
		return resultTxt;
	}
	
	/**
	 * 生成要替换的字符串
	 * @param replaceChar
	 * @param length
	 * @return
	 */
	private String getReplaceChars(String replaceChar,int length){
		StringBuffer resultReplace =new StringBuffer();
		for(int i = 0 ; i < length ; i++)
			resultReplace.append(replaceChar);
		return resultReplace.toString();
	}
	
	/**
	 * 更换敏感词库
	 * @param config
	 */
	public synchronized void resetConfig(SensitiveWordConfig config){
		this.config=config;
		this.config.initKeyWordConfig();
	}
	
	/**
	 * 添加新的敏感词到敏感词库中
	 * @param keyWordSet
	 */
	public synchronized void addSensitiveWord(Set<String> keyWordSet) {  
		this.config.addSensitiveWordToHashMap(keyWordSet);
	}
}
