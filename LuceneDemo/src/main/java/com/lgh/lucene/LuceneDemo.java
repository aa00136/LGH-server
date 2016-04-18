package com.lgh.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.lgh.util.FileUtils;

public class LuceneDemo {
	private Analyzer analyzer;
	private IndexWriterConfig config;
	private IndexWriter indexWriter;
	private Directory index;

	public LuceneDemo(String path) throws IOException {
		super();
		File file=new File(path);
		this.analyzer = new StandardAnalyzer();
		this.config = new IndexWriterConfig(analyzer);
		this.index = new SimpleFSDirectory(file.toPath());
		this.indexWriter = new IndexWriter(index, config);
	}

	private static void addDoc(IndexWriter w, String title, String fileName) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("fileName", fileName, Field.Store.YES));
		doc.add(new TextField("body", title, Field.Store.YES));
		w.addDocument(doc);
	}

	public boolean createIndex(String path) {
		try {
			List<File> fileList = FileUtils.getDocxFiles(path);
			for (File file : fileList) {
				addDoc(indexWriter, FileUtils.docToString(file), file.getName());
			}
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Map<String, String> searchIndex(String queryStr) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		Query query = new QueryBuilder(analyzer).createPhraseQuery("body", queryStr);
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("fileName") + "\t" + d.get("body"));
			resultMap.put(d.get("fileName"), d.get("body"));
		}
		reader.close();

		return resultMap;
	}

	public static void main(String[] args) throws Exception {
		LuceneDemo demo = new LuceneDemo("E:/java备份/测试/lucene");
		demo.createIndex("E:/学习资料/大学/选修/");
		demo.searchIndex("现代生活化学");
	}

}
