package com.lgh.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.lgh.dataStruct.TrieMap;
import com.lgh.lucene.LuceneDemo;

import redis.clients.jedis.Jedis;

public class SensitiveWordTest {

	@Test
	public void test() throws IOException {
		LuceneDemo demo = new LuceneDemo("E:/java备份/测试/lucene");
		Map<String, String> resultMap = demo.searchIndex("现代生活化学");
		Set<String> keySet = resultMap.keySet();
		SensitiveWordConfig config = new SensitiveWordConfig("GBK", "E:/java备份/测试/敏感词过滤/敏感词库/sensitiveWord.txt");
		SensitiveWordFilter filter = new SensitiveWordFilter(config);
		/*Set<String> newString=new HashSet<String>();
		newString.add("与");
		filter.addSensitiveWordToHashMap(newString);*/
		System.out.println("敏感词库中的敏感词个数为："+config.getSensitiveWordCount());
		for (String key : keySet) {
			String txt = resultMap.get(key);
			Set<String> set = filter.getSensitiveWord(txt, 1);
			txt = filter.replaceSensitiveWord(txt, 2, "*");
			System.out.println("语句中包含敏感词的种类为：" + set.size() + "。包含：" + set);
			System.out.println(txt);
		}
	}
	@Test
	public void testConfig() throws IOException {
		SensitiveWordConfig config = new SensitiveWordConfig("GBK", "E:/java备份/测试/敏感词过滤/敏感词库/sensitiveWord.txt");
		config.initKeyWordConfig();
		System.out.println("敏感词库中的敏感词个数为："+config.getSensitiveWordCount());
	}
	
	@Test
	public void testTrieMap() throws IOException {
		Jedis jedis=new Jedis("10.21.32.113");
		TrieMap trieMap = new TrieMap("GBK", "E:/java备份/测试/敏感词过滤/敏感词库/sensitiveWord.txt");
		trieMap.initKeyWordConfig();
		Set<String>keySet=trieMap.wordRateMap.keySet();
		for(String key:keySet){
			System.out.println(key+"  "+trieMap.wordRateMap.get(key));
		}
		jedis.zadd("wordList", trieMap.wordRateMap);
		System.out.println(jedis.zrevrange("wordList", 0, -1));
		jedis.close();
	}
}
