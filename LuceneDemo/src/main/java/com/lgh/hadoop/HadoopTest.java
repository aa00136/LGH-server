package com.lgh.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

public class HadoopTest {

	@Test
	public void test() {
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
		InputStream in = null;
		try {
			in = new URL("hdfs://10.21.32.113:9000/user/hadoop/input/core-site.xml").openStream();
			IOUtils.copyBytes(in, System.out, 4096, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test1() {
		CharsetDecoder decoder = Charset.forName("utf-8").newDecoder(); // 解码
		String hdfs = "hdfs://10.21.32.113:9000/user/hadoop/input/core-site.xml";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(hdfs), conf);
			FSDataInputStream in = fs.open(new Path(hdfs));
			ByteBuffer ioBuffer = ByteBuffer.allocate(4096);
			ioBuffer.clear();
			in.read(ioBuffer);
			ioBuffer.flip();
			CharBuffer charBuffer = decoder.decode(ioBuffer);
			System.out.println(charBuffer.toString());
			in.close();
			fs.close();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		String hdfs = "hdfs://10.21.32.113:9000/user/hadoop/input/test.txt";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(hdfs), conf);
			FSDataInputStream in = fs.open(new Path(hdfs));
			byte[] ioBuffer = new byte[1024];
			int readLen = in.read(ioBuffer);
			while (readLen != -1) {
				System.out.write(ioBuffer, 0, readLen);
				readLen = in.read(ioBuffer);
			}
			in.close();
			fs.close();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test3() {		
		String hdfs = "hdfs://10.21.32.113:9000/user/hadoop/input/test.txt";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(hdfs), conf);
			// FSDataInputStream in = fs.open(new Path(hdfs));
			FSDataOutputStream out = fs.create(new Path(hdfs));
			for(int i=0;i<100000000;i++){
				out.writeUTF("hello world!"+i);
			}
			out.close();
			fs.close();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
